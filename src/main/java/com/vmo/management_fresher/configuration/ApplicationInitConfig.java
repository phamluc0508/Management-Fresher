package com.vmo.management_fresher.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.RoleRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_USER_NAME = "admin";
    private static final String SYSTEM_NAME = "system";

    @Value("${admin.password}")
    private String adminPassword;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "org.postgresql.Driver")
    ApplicationRunner applicationRunner(AccountRepo accountRepo, RoleRepo roleRepo) {
        return args -> {
            if (!accountRepo.existsByUsername(ADMIN_USER_NAME)) {
                Role adminRole = Role.builder()
                        .name(Constant.ADMIN_ROLE)
                        .description("Admin role")
                        .createdBy(SYSTEM_NAME)
                        .updatedBy(SYSTEM_NAME)
                        .build();
                roleRepo.save(adminRole);

                Role otherRole = Role.builder()
                        .name(Constant.OTHER_ROLE)
                        .description("Other role")
                        .createdBy(SYSTEM_NAME)
                        .updatedBy(SYSTEM_NAME)
                        .build();
                roleRepo.save(otherRole);

                accountRepo.save(Account.builder()
                        .username(ADMIN_USER_NAME)
                        .password(passwordEncoder.encode(adminPassword))
                        .role(adminRole)
                        .createdBy(SYSTEM_NAME)
                        .updatedBy(SYSTEM_NAME)
                        .build());
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
