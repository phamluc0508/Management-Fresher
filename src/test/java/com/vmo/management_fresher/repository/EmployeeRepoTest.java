package com.vmo.management_fresher.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import com.vmo.management_fresher.model.Employee;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource("/repo-test.properties")
public class EmployeeRepoTest {

    @Autowired
    private EmployeeRepo employeeRepo;

    private Employee employee;

    @BeforeEach
    void initData() {
        String uid = "testUid";
        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .middleName("Doe")
                .lastName("Smith")
                .email("test@gmail.com")
                .phoneNumber("0123456789")
                .accountId("axbycz147")
                .createdBy(uid)
                .updatedBy(uid)
                .build();
    }

    @Test
    void saveEmployee_Test_Success() {
        // GIVEN

        // WHEN
        Employee result = employeeRepo.save(employee);

        // THEN
        Assertions.assertThat(result.getId()).isNotNull();
        Assertions.assertThat(result.getId()).isGreaterThan(0);
    }
}
