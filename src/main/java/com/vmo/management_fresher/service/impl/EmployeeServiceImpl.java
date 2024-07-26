package com.vmo.management_fresher.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.base.validate.Validate;
import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.repository.RoleRepo;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.service.EmployeeService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepo repo;
    private final AccountRepo accountRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;
    private final EmployeeRepo employeeRepo;
    private final RoleRepo roleRepo;

    public void valid(EmployeeReq request) {
        if (StringUtils.isEmpty(request.getFirstName())) {
            throw new RuntimeException("first-name-cannot-be-null");
        }
        if (StringUtils.isEmpty(request.getMiddleName())) {
            throw new RuntimeException("middle-name-cannot-be-null");
        }
        if (StringUtils.isEmpty(request.getLastName())) {
            throw new RuntimeException("last-name-cannot-be-null");
        }
        if (StringUtils.isEmpty(request.getEmail())) {
            throw new RuntimeException("email-cannot-be-null");
        } else if (!Pattern.matches(Validate.EMAIL_REGEX, request.getEmail())) {
            throw new RuntimeException("invalid-email-format");
        }
        if (StringUtils.isEmpty(request.getPhoneNumber())) {
            throw new RuntimeException("phone-number-cannot-be-null");
        } else if (!Pattern.matches(Validate.PHONE_REGEX, request.getPhoneNumber())) {
            throw new RuntimeException("invalid-phone-format");
        }
    }

    @Override
    public EmployeeRes createEmployee(String uid, EmployeeReq request) {
        valid(request);
        if (repo.existsByEmail(request.getEmail())) {
            throw new EntityExistsException("email-used");
        }
        if (repo.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new EntityExistsException("phone-number-used");
        }

        Employee employee = Employee.of(uid, request);

        // create Account for new Employee
        Account account = new Account();
        account.setUsername(request.getEmail());
        account.setPassword(passwordEncoder.encode(request.getPhoneNumber()));
        Role role = roleRepo.findById(Constant.OTHER_ROLE)
                .orElseThrow(() -> new EntityNotFoundException("role-not-found"));
        account.setRole(role);
        account.setCreatedBy(uid);
        account.setUpdatedBy(uid);
        account = accountRepo.save(account);

        employee.setAccountId(account.getId());
        employee = repo.save(employee);

        EmployeeRes employeeRes = employee.toRES();
        employeeRes.setAccountId(account.getId());
        employeeRes.setUsername(account.getUsername());

        return employeeRes;
    }

    @Override
    public Employee updateEmployee(String uid, Long id, EmployeeReq request) {
        if (!authenticationService.checkAdminRole(uid)
                && !authenticationService.checkIsMyself(uid, id)
                && !authenticationService.checkDirectorFresher(uid, id)) {
            throw new AccessDeniedException("no-permission");
        }

        valid(request);
        Employee employee =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        if (repo.existsByEmailAndIdIsNot(request.getEmail(), id)) {
            throw new EntityExistsException("email-used");
        }
        employee.setEmail(request.getEmail());
        if (repo.existsByPhoneNumberAndIdIsNot(request.getPhoneNumber(), id)) {
            throw new EntityExistsException("phone-number-used");
        }
        employee.setPhoneNumber(request.getPhoneNumber());

        employee.setUpdatedBy(uid);

        return repo.save(employee);
    }

    @Override
    public String deleteEmployee(Long id) {
        if (employeeCenterRepo.existsByEmployeeId(id)) {
            throw new EntityExistsException("employee-is-currently-in-the-center");
        }
        Employee employee =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
        accountRepo.deleteById(employee.getAccountId());
        repo.delete(employee);

        return "Success!";
    }

    @Override
    public Map<String, Object> getById(String uid, Long id) {
        if (!authenticationService.checkAdminRole(uid)
                && !authenticationService.checkIsMyself(uid, id)
                && !authenticationService.checkDirectorFresher(uid, id)) {
            throw new AccessDeniedException("no-permission");
        }
        var employee = repo.getEmployeeById(id)
                .orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
        List<Map<String, Object>> employeeCenter = employeeCenterRepo.getByEmployeeId(id);
        Map<String, Object> result = new HashMap<>(employee);
        result.put("position", employeeCenter);
        return result;
    }

    @Override
    public List<Map<String, Object>> getAll() {
        return repo.getAll();
    }

    @Override
    public Page<Employee> searchEmployee(
            String uid, String name, String email, String position, String programingLanguage, Pageable pageable) {
        Page<Employee> result = null;
        if (authenticationService.checkAdminRole(uid)) {
            result = repo.searchEmployee(name, email, position, programingLanguage, pageable);
        } else if (authenticationService.checkDirectorRole(uid)) {
            Employee employee = employeeRepo
                    .findByAccountId(uid)
                    .orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
            List<Long> centerIds =
                    employeeCenterRepo.findCenterIdsByDirectorId(employee.getId(), Constant.DIRECTOR_POSITION);
            result = repo.searchEmployeeByCenterIds(name, email, position, programingLanguage, centerIds, pageable);
        }
        return result;
    }
}
