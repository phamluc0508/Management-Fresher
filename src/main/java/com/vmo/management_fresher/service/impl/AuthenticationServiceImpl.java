package com.vmo.management_fresher.service.impl;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AuthenticationRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.service.AuthenticationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepo accountRepo;

    @Value("${jwt.signer.key}")
    private String SIGNER_KEY;

    @Override
    public AuthenticationRes login(AuthenticationReq request){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        Account account = accountRepo.findByUsername(request.getUsername()).orElseThrow(() -> new EntityNotFoundException("not-found-with-username: " + request.getUsername()));
        if(!passwordEncoder.matches(request.getPassword(), account.getPassword())){
            throw new RuntimeException("wrong-password");
        }

        AuthenticationRes result = new AuthenticationRes();
        result.setToken(generateToken(account));

        return result;
    }

    private String generateToken(Account account){
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(account.getId())
                .issuer("localhost:8080")
                .issueTime(new Date())
                .expirationTime(new Date(Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
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

    private String buildScope(Account account){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(account.getRoles())){
            account.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
//                if(!CollectionUtils.isEmpty(role.getPermissions())){
//                    role.getPermissions().forEach(permission -> {
//                        stringJoiner.add(permission.getName());
//                    });
//                }

            });
        }
        return stringJoiner.toString();
    }

    private SignedJWT verifyToken(String token){
        try {
            JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
            SignedJWT signedJWT = SignedJWT.parse(token);

            Date expityTime = signedJWT.getJWTClaimsSet().getExpirationTime();

            var verified = signedJWT.verify(verifier);
            if (!(verified && expityTime.after(new Date()))) {
                throw new RuntimeException("User-is-not-authenticated");
            }

            return signedJWT;
        } catch (JOSEException | ParseException ex){
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Boolean introspect(String token){
        try{
            verifyToken(token);
        } catch (Exception ex){
            return false;
        }

        return true;
    }

}
