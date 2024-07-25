package com.vmo.management_fresher.service.impl;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vmo.management_fresher.exception.ExpectationFailedException;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final EmployeeRepo employeeRepo;
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${spring.mail.username}")
    private String MAIL_FROM;

    @Value("${otp.email.expiration}")
    private Long OTP_EXPIRATION;

    private Integer otpGenerator() {
        return 100_000 + secureRandom.nextInt(900_000); // Generates a number between 100_000 and 999_999
    }

    @Override
    public String verifyEmail(String email) {
        Employee employee =
                employeeRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("invalid-email"));
        Integer otp = otpGenerator();
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(MAIL_FROM);
        message.setTo(email);
        message.setSubject("OTP for Forgot Password request");
        message.setText("This is the OTP for your Forgot Password request: " + otp);
        javaMailSender.send(message);

        redisTemplate
                .opsForValue()
                .set(String.valueOf(otp), employee.getAccountId(), OTP_EXPIRATION, TimeUnit.MILLISECONDS);

        return "Email sent for verification";
    }

    private void verifyOtp(String accountId, Integer otp) {
        String value = redisTemplate.opsForValue().get(String.valueOf(otp));
        if (value == null || !value.equals(accountId)) {
            throw new ExpectationFailedException("otp-has-expired");
        }
    }

    @Override
    public String changePassword(String password, String repeatPassword, String email, Integer otp) {
        Employee employee =
                employeeRepo.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("invalid-email"));

        verifyOtp(employee.getAccountId(), otp);

        if (!Objects.equals(password, repeatPassword)) {
            throw new ExpectationFailedException("please-enter-password-again");
        }

        redisTemplate.delete(String.valueOf(otp));

        String encodePassword = passwordEncoder.encode(password);
        accountRepo.updatePassword(employee.getAccountId(), encodePassword);
        return "password-has-been-changed";
    }
}
