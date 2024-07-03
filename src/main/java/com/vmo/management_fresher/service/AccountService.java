package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.response.AccountRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepo repo;
    private final EmployeeRepo employeeRepo;
    private final PasswordEncoder passwordEncoder;

    public void valid(Account request){
        if(StringUtils.isEmpty(request.getUsername())){
            throw new RuntimeException("username-cannot-be-null");
        }
        if(StringUtils.isEmpty(request.getPassword())){
            throw new RuntimeException("password-cannot-be-null");
        }
    }

    public String createAccount(String uid, Account request){
        valid(request);

        var exist = repo.existsByUsername(request.getUsername());
        if(exist){
            throw new EntityExistsException("username-existed");
        }
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        request.setCreatedBy(uid);
        request.setUpdatedBy(uid);

        repo.save(request);

        //create Employee for new Account
        Employee employee = new Employee();
        employee.setAccountId(request.getId());
        employee.setCreatedBy(uid);
        employee.setUpdatedBy(uid);
        employeeRepo.save(employee);

        return "Success!";
    }

    public String updateAccount(String uid, String id, Account request){
        valid(request);
        var exist = repo.existsByUsernameAndIdIsNot(request.getUsername(), id);
        if(exist){
            throw new EntityExistsException("username-existed");
        }
        Account account = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found-with-id: " + id));
        account.setUsername(request.getUsername());
        account.setPassword(passwordEncoder.encode(request.getPassword()));
        account.setUpdatedBy(uid);

        repo.save(request);

        return "Success!";
    }

    public AccountRes getById(String id){
        AccountRes result = new AccountRes();
        Account account = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Account-not-found-with-id: " + id));
        result.setId(id);
        result.setUsername(account.getUsername());
        Set<String> roles = new HashSet<>();
        for(Role role : account.getRoles()){
            roles.add(role.getName());
        }
        result.setRoles(roles);
        return result;
    }

    public List<AccountRes> getAll(){
        List<AccountRes> result = new ArrayList<>();
        List<Account> accounts = repo.findAll();
        for(Account account : accounts){
            AccountRes accountRes = new AccountRes();
            accountRes.setId(account.getId());
            accountRes.setUsername(account.getUsername());
            Set<String> roles = new HashSet<>();
            for(Role role : account.getRoles()){
                roles.add(role.getName());
            }
            accountRes.setRoles(roles);

            result.add(accountRes);
        }
        return result;
    }
}
