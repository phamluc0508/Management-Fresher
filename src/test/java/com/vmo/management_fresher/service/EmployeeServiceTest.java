package com.vmo.management_fresher.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.RoleRepo;
import jakarta.persistence.EntityExistsException;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;

import java.util.Optional;

@SpringBootTest
@TestPropertySource("/service-test.properties")
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;

    @MockBean
    private EmployeeRepo employeeRepo;

    @MockBean
    private RoleRepo roleRepo;

    @MockBean
    private AccountRepo accountRepo;

    private EmployeeReq request;
    private Employee employee;
    private Account account;
    private Role role;

    @BeforeEach
    void initData() {
        request = EmployeeReq.builder()
                .firstName("John")
                .middleName("Doe")
                .lastName("Smith")
                .email("test@gmail.com")
                .phoneNumber("0123456789")
                .build();

        employee = Employee.builder()
                .id(1L)
                .firstName("John")
                .middleName("Doe")
                .lastName("Smith")
                .email("test@gmail.com")
                .phoneNumber("0123456789")
                .accountId("axbycz147")
                .build();

        account = Account.builder().id("axbycz147").username("test@gmail.com").build();

        role = Role.builder().name("OTHER").description("Other role").build();
    }

    @Test
    void createEmployee_Test_Success() {
        // GIVEN
        Mockito.when(employeeRepo.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(employeeRepo.existsByPhoneNumber(ArgumentMatchers.anyString()))
                .thenReturn(false);
        Mockito.when(roleRepo.findById(Constant.OTHER_ROLE)).thenReturn(Optional.ofNullable(role));
        Mockito.when(accountRepo.save(ArgumentMatchers.any())).thenReturn(account);
        Mockito.when(employeeRepo.save(ArgumentMatchers.any())).thenReturn(employee);

        // WHEN
        EmployeeRes result = employeeService.createEmployee(ArgumentMatchers.anyString(), request);

        // THEN
        Assertions.assertThat(result.getAccountId()).isEqualTo("axbycz147");
        Assertions.assertThat(result.getEmail()).isEqualTo("test@gmail.com");
        Assertions.assertThat(result.getUsername()).isEqualTo("test@gmail.com");
    }

    @Test
    void createEmployee_Test_Fail() {
        // GIVEN
        Mockito.when(employeeRepo.existsByEmail(ArgumentMatchers.anyString())).thenReturn(false);
        Mockito.when(employeeRepo.existsByPhoneNumber(ArgumentMatchers.anyString()))
                .thenReturn(true);

        // WHEN
        var exception = assertThrows(
                EntityExistsException.class,
                () -> employeeService.createEmployee(ArgumentMatchers.anyString(), request));

        // THEN
        Assertions.assertThat(exception.getMessage()).isEqualTo("phone-number-used");
    }
}
