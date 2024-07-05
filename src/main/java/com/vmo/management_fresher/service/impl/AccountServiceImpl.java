package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.response.AccountRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.repository.RoleRepo;
import com.vmo.management_fresher.service.AccountService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepo repo;
    private final RoleRepo roleRepo;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final PasswordEncoder passwordEncoder;

    private void valid(Account request){
        if(StringUtils.isEmpty(request.getUsername())){
            throw new RuntimeException("username-cannot-be-null");
        }
        if(StringUtils.isEmpty(request.getPassword())){
            throw new RuntimeException("password-cannot-be-null");
        }
    }

    @Override
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

    @Override
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

    @Override
    public String deleteAccount(String id){
        Account account = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("account-not-found-with-id: " + id));

        Employee employee = employeeRepo.findByAccountId(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-accountId: " + id));
        if(employeeCenterRepo.existsByEmployeeId(employee.getId())){
            throw new EntityExistsException("employee-is-currently-in-the-center");
        }
        employeeRepo.delete(employee);
        repo.delete(account);

        return "Success!";
    }

    @Override
    public Account addRoleAccount(String uid, String id, List<String> roles){
        Account account = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("Account-not-found-with-id: " + id));
        if(account.getUsername().equals("admin")){
            throw new RuntimeException("invalid-id: " + id);
        }
        Set<String> roleValid = new HashSet<>();
        for(String r : roles){
            if(r.equals(Constant.ADMIN_ROLE)){
                throw new RuntimeException("role-cannot-be-admin");
            } else if(!(r.equals(Constant.DIRECTOR_ROLE) || r.equals(Constant.FRESHER_ROLE))){
                throw new RuntimeException("invalid-role-format");
            }
            roleValid.add(r);
        }

        List<Role> roleList = roleRepo.findAllById(roleValid);
        account.setRoles(new HashSet<>(roleList));
        account.setUpdatedBy(uid);

        return repo.save(account);
    }

    @Override
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

    @Override
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
