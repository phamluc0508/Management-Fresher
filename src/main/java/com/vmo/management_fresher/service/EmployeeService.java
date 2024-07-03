package com.vmo.management_fresher.service;

import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepo repo;
    private final AccountRepo accountRepo;
    private final PasswordEncoder passwordEncoder;

    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final String PHONE_REGEX = "^0\\d{9}$";

    public void valid(Employee request){
        if(StringUtils.isEmpty(request.getFirstName())){
            throw new RuntimeException("first-name-cannot-be-null");
        }
        if(StringUtils.isEmpty(request.getMiddleName())){
            throw new RuntimeException("middle-name-cannot-be-null");
        }
        if(StringUtils.isEmpty(request.getLastName())){
            throw new RuntimeException("last-name-cannot-be-null");
        }
        if(StringUtils.isEmpty(request.getEmail())){
            throw new RuntimeException("email-cannot-be-null");
        } else if(!Pattern.matches(EMAIL_REGEX, request.getEmail())){
            throw new RuntimeException("invalid-email-format");
        }
        if(StringUtils.isEmpty(request.getPhoneNumber())){
            throw new RuntimeException("phone-number-cannot-be-null");
        } else if(!Pattern.matches(PHONE_REGEX, request.getPhoneNumber())){
            throw new RuntimeException("invalid-phone-format");
        }
    }

    public String createEmployee(String uid, Employee request){
        valid(request);
        if(repo.existsByEmail(request.getEmail())){
            throw new EntityNotFoundException("email-used");
        }
        if(repo.existsByPhoneNumber(request.getPhoneNumber())){
            throw new EntityNotFoundException("phone-number-used");
        }
        request.setCreatedBy(uid);
        request.setUpdatedBy(uid);

        //create Account for new Employee
        Account account = new Account();
        account.setUsername(request.getEmail());
        account.setPassword(passwordEncoder.encode("123456"));
        account.setCreatedBy(uid);
        account.setUpdatedBy(uid);
        accountRepo.save(account);

        request.setAccountId(account.getId());
        repo.save(request);

        return "Success!";
    }

    public Employee updateEmployee(String uid, Long id, Employee request){
        valid(request);
        Employee employee = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + id));
        employee.setFirstName(request.getFirstName());
        employee.setMiddleName(request.getMiddleName());
        employee.setLastName(request.getLastName());
        if(repo.existsByEmailAndIdIsNot(request.getEmail(), id)){
            throw new EntityNotFoundException("email-used");
        }
        employee.setEmail(request.getEmail());
        if(repo.existsByPhoneNumberAndIdIsNot(request.getPhoneNumber(), id)){
            throw new EntityNotFoundException("phone-number-used");
        }
        employee.setPhoneNumber(request.getPhoneNumber());

        employee.setUpdatedBy(uid);

        return repo.save(employee);
    }

    public Map<String, Object> getById(Long id){
        return repo.getEmployeeById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + id));
    }

    public List<Map<String, Object>> getAll(){
        return repo.getAll();
    }
}
