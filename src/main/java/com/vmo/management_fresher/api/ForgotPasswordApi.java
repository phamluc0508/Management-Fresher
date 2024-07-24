package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmo.management_fresher.service.EmailService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/forgot-password")
@RequiredArgsConstructor
@Tag(name = "ForgotPassword", description = "ForgotPassword API ")
@SecurityRequirements
public class ForgotPasswordApi {
    private final EmailService emailService;

    // send-otp-email
    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify email",
            description = "Check if the provided email has already been used",
            tags = {"ForgotPassword"})
    protected ResponseEntity verifyEmail(
            @Parameter(description = "User's email", required = true) @RequestParam String email) {
        try {
            return ResponseUtils.handlerSuccess(emailService.verifyEmail(email));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    // change-password-with-otp
    @PostMapping("/change-password")
    @Operation(
            summary = "Change password",
            description = "Change the user's password using the provided email, password, and OTP",
            tags = {"ForgotPassword"})
    protected ResponseEntity changePassword(
            @Parameter(description = "New password", required = true) @RequestParam String password,
            @Parameter(description = "Repeat new password", required = true) @RequestParam String repeatPassword,
            @Parameter(description = "User's email", required = true) @RequestParam String email,
            @Parameter(description = "One-Time Password (OTP)", required = true) @RequestParam Integer otp) {
        try {
            return ResponseUtils.handlerSuccess(emailService.changePassword(password, repeatPassword, email, otp));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
