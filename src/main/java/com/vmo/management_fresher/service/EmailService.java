package com.vmo.management_fresher.service;

public interface EmailService {
    String verifyEmail(String email);
    String changePassword(String password, String repeatPassword, String email, Integer otp);
}
