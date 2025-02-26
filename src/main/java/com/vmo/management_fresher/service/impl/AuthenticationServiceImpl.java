package com.vmo.management_fresher.service.impl;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AuthenticationRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.EmployeeCenter;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "authenCache")
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepo accountRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final EmployeeRepo employeeRepo;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.signer.key}")
    private String SIGNER_KEY;

    @Value("${jwt.expiration}")
    private Long TOKEN_EXPIRATION;

    @Value("${jwt.refresh.expiration}")
    private Long REFRESH_EXPIRATION;

    @Override
    public AuthenticationRes login(AuthenticationReq request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        Account account = accountRepo
                .findByUsername(request.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        if (!passwordEncoder.matches(request.getPassword(), account.getPassword())) {
            throw new RuntimeException("wrong-password");
        }

        AuthenticationRes result = new AuthenticationRes();
        result.setToken(generateToken(account));

        return result;
    }

    private String generateToken(Account account) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getId())
                .issuer("localhost:8080")
                .issueTime(new Date(System.currentTimeMillis()))
                //                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .expirationTime(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(account))
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        return jwsObject.serialize();
    }

    private String buildScope(Account account) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!StringUtils.isEmpty(account.getRole())) {
            stringJoiner.add("ROLE_" + account.getRole().getName());
        }
        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token, Boolean isRefresh, String uid) {
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expityTime = (isRefresh)
                    ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().getTime() + REFRESH_EXPIRATION)
                    : signedJWT.getJWTClaimsSet().getExpirationTime();
            String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
            String accountId = signedJWT.getJWTClaimsSet().getSubject();

            var verified = signedJWT.verify(verifier);
            if (!(verified && expityTime.after(new Date()))
                    || isTokenBlacklisted(jwtID)
                    || (uid != null && !uid.equals(accountId))) {
                throw new InsufficientAuthenticationException("Unauthorized!");
            }

            return signedJWT;
        } catch (JOSEException | ParseException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Boolean introspect(String token) {
        // spotless:off
        try {
            verifyToken(token, false, null);
        } catch (Exception ex) {
            return false;
        }
        // spotless:on
        return true;
    }

    @Override
    public String logout(String uid, String token) {
        try {
            SignedJWT signedJWT = verifyToken(token, true, uid);
            String jwtID = signedJWT.getJWTClaimsSet().getJWTID();
            blacklistToken(token);

        } catch (InsufficientAuthenticationException e) {
            log.info("Token already expired");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return "Logout success!";
    }

    @Override
    public AuthenticationRes refreshToken(String uid, String token) {
        try {
            SignedJWT signedJWT = verifyToken(token, true, uid);
            String jwtID = signedJWT.getJWTClaimsSet().getJWTID();

            String accountId = signedJWT.getJWTClaimsSet().getSubject();
            Account account = accountRepo
                    .findById(accountId)
                    .orElseThrow(() -> new EntityNotFoundException("account-not-found"));

            blacklistToken(token);

            AuthenticationRes result = new AuthenticationRes();
            result.setToken(generateToken(account));

            return result;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private void blacklistToken(String token) {
        try {
            // Parse the JWT
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Get the JWT ID
            String jwtID = claimsSet.getJWTID();

            // Calculate the remaining time until expiration
            long currentTimeMillis = System.currentTimeMillis();
            long expirationTimeMillis =
                    signedJWT.getJWTClaimsSet().getIssueTime().getTime() + REFRESH_EXPIRATION;
            long remainingTimeMillis = expirationTimeMillis - currentTimeMillis;

            // Only blacklist if the token is not already expired
            if (remainingTimeMillis > 0) {
                log.info("Attempting to blacklist token with ID: {}", jwtID);
                redisTemplate.opsForValue().set(jwtID, "blacklisted", remainingTimeMillis, TimeUnit.MILLISECONDS);
                log.info("Token blacklisted successfully");
            } else {
                log.info("Token is already expired, no need to blacklist");
            }
        } catch (ParseException e) {
            log.error("Error parsing JWT token: ", e);
        } catch (Exception e) {
            log.error("Error blacklisting token: ", e);
        }
    }

    private Boolean isTokenBlacklisted(String jwtID) {
        log.info("Checking if token is blacklisted: {}", jwtID);
        Boolean result = Boolean.TRUE.equals(redisTemplate.hasKey(jwtID));
        log.info("Token blacklist status: {}", result);
        return result;
    }

    @Cacheable(cacheNames = "checkAdminRole", key = "#uid")
    @Override
    public Boolean checkAdminRole(String uid) {
        log.info("Checking admin role for user: {}", uid);
        Account account = accountRepo
                .findById(uid)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        boolean isAdmin = account.getRole().getName().equals(Constant.ADMIN_ROLE);
        log.info("User {} is admin: {}", uid, isAdmin);
        return isAdmin;
    }

    @Cacheable(key = "#uid")
    @Override
    public Boolean checkDirectorRole(String uid) {
        Account account = accountRepo
                .findById(uid)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        if (account.getRole().getName().equals(Constant.DIRECTOR_ROLE)) {
            return true;
        }
        return false;
    }

    @Cacheable(key = "#uid")
    @Override
    public Boolean checkFresherRole(String uid) {
        Account account = accountRepo
                .findById(uid)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        if (account.getRole().getName().equals(Constant.FRESHER_ROLE)) {
            return true;
        }
        return false;
    }

    @Cacheable(key = "#uid + '-' + #employeeId")
    @Override
    public Boolean checkIsMyself(String uid, Long employeeId) {
        Employee employee =
                employeeRepo.findById(employeeId).orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
        if (employee.getAccountId().equals(uid)) {
            return true;
        }
        return false;
    }

    @Cacheable(key = "#directorAccId + '-' + #fresherId")
    @Override
    public Boolean checkDirectorFresher(String directorAccId, Long fresherId) {
        var fresherCenter = employeeCenterRepo.findByEmployeeIdAndPositionName(fresherId, Constant.FRESHER_POSITION);
        if (fresherCenter.isEmpty()) {
            return false;
        }

        Employee director = employeeRepo
                .findByAccountId(directorAccId)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        List<EmployeeCenter> employeeCenters =
                employeeCenterRepo.findAllByEmployeeIdAndPositionName(director.getId(), Constant.DIRECTOR_POSITION);
        for (EmployeeCenter employeeCenter : employeeCenters) {
            if (employeeCenter.getCenterId() == fresherCenter.get().getCenterId()) {
                return true;
            }
        }
        return false;
    }

    @Cacheable(key = "#directorAccId + '-' + #centerId")
    @Override
    public Boolean checkDirectorCenter(String directorAccId, Long centerId) {
        Employee director = employeeRepo
                .findByAccountId(directorAccId)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        var directorCenter = employeeCenterRepo.findByEmployeeIdAndCenterIdAndPositionName(
                director.getId(), centerId, Constant.DIRECTOR_POSITION);
        if (directorCenter.isPresent()) {
            return true;
        }
        return false;
    }

    @Cacheable(key = "#employeeAccId + '-' + #centerId")
    @Override
    public Boolean checkEmployeeCenter(String employeeAccId, Long centerId) {
        Employee employee = employeeRepo
                .findByAccountId(employeeAccId)
                .orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        var employeeCenter = employeeCenterRepo.findByEmployeeIdAndCenterId(employee.getId(), centerId);
        if (employeeCenter.isPresent()) {
            return true;
        }
        return false;
    }
}
