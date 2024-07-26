package com.vmo.management_fresher.service.impl;

import java.util.*;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.dto.response.AccountRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.repository.RoleRepo;
import com.vmo.management_fresher.service.AccountService;
import com.vmo.management_fresher.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo repo;
    private final RoleRepo roleRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final AuthenticationService authenticationService;
    private final PasswordEncoder passwordEncoder;

    private void valid(AuthenticationReq request) {
        if (StringUtils.isEmpty(request.getUsername())) {
            throw new RuntimeException("username-cannot-be-null");
        }
        if (StringUtils.isEmpty(request.getPassword())) {
            throw new RuntimeException("password-cannot-be-null");
        }
    }

    @Override
    public String createAccount(String uid, AuthenticationReq request) {
        valid(request);

        var exist = repo.existsByUsername(request.getUsername());
        if (exist) {
            throw new EntityExistsException("username-existed");
        }
        Account account = new Account();
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role = roleRepo.findById(Constant.OTHER_ROLE)
                .orElseThrow(() -> new EntityNotFoundException("role-not-found"));
        account.setRole(role);

        account.setCreatedBy(uid);
        account.setUpdatedBy(uid);

        repo.save(account);

        // create Employee for new Account
        Employee employee = new Employee();
        employee.setAccountId(account.getId());
        employee.setCreatedBy(uid);
        employee.setUpdatedBy(uid);
        employeeRepo.save(employee);

        return "Success!";
    }

    @Override
    public String updateAccount(String uid, String id, AuthenticationReq request) {
        if (!authenticationService.checkAdminRole(uid) && !uid.equals(id)) {
            throw new AccessDeniedException("no-permission");
        }
        valid(request);
        var exist = repo.existsByUsernameAndIdIsNot(request.getUsername(), id);
        if (exist) {
            throw new EntityExistsException("username-existed");
        }
        Account account =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setUpdatedBy(uid);

        repo.save(account);

        return "Success!";
    }

    @Override
    public String deleteAccount(String id) {
        Account account =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found"));

        Employee employee = employeeRepo
                .findByAccountId(id)
                .orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
        if (employeeCenterRepo.existsByEmployeeId(employee.getId())) {
            throw new EntityExistsException("employee-is-currently-in-the-center");
        }
        employeeRepo.delete(employee);
        repo.delete(account);

        return "Success!";
    }

    @CacheEvict(cacheNames = "authenCache", allEntries = true)
    @Override
    public Account addRoleAccount(String uid, String id, String roleName) {
        Account account =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        if (account.getUsername().equals("admin")) {
            throw new RuntimeException("invalid-id");
        }
        if (roleName.equals(Constant.ADMIN_ROLE)) {
            throw new RuntimeException("role-cannot-be-admin");
        } else if (!(roleName.equals(Constant.DIRECTOR_ROLE)
                || roleName.equals(Constant.FRESHER_ROLE)
                || roleName.equals(Constant.OTHER_ROLE))) {
            throw new RuntimeException("invalid-role-format");
        }

        Role role = roleRepo.findById(roleName).orElseThrow(() -> new EntityNotFoundException("role-not-found"));
        account.setRole(role);
        account.setUpdatedBy(uid);

        return repo.save(account);
    }

    @Override
    public AccountRes getById(String uid, String id) {
        if (!authenticationService.checkAdminRole(uid) && !uid.equals(id)) {
            throw new AccessDeniedException("no-permission");
        }

        AccountRes result = new AccountRes();
        Account account =
                repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found"));
        result.setId(id);
        result.setUsername(account.getUsername());
        String role = account.getRole().getName();
        result.setRole(role);
        return result;
    }

    @Override
    public List<AccountRes> getAll() {
        List<AccountRes> result = new ArrayList<>();
        List<Account> accounts = repo.findAll();
        for (Account account : accounts) {
            AccountRes accountRes = new AccountRes();
            accountRes.setId(account.getId());
            accountRes.setUsername(account.getUsername());
            String role = account.getRole().getName();
            accountRes.setRole(role);

            result.add(accountRes);
        }
        return result;
    }
}
