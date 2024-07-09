package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.base.validate.Validate;
import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.AccountRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.service.EmployeeService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepo repo;
    private final AccountRepo accountRepo;
    private final EmployeeCenterRepo employeeCenterRepo;
    private final PasswordEncoder passwordEncoder;

    public void valid(EmployeeReq request){
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
        } else if(!Pattern.matches(Validate.EMAIL_REGEX, request.getEmail())){
            throw new RuntimeException("invalid-email-format");
        }
        if(StringUtils.isEmpty(request.getPhoneNumber())){
            throw new RuntimeException("phone-number-cannot-be-null");
        } else if(!Pattern.matches(Validate.PHONE_REGEX, request.getPhoneNumber())){
            throw new RuntimeException("invalid-phone-format");
        }
    }

    @Override
    public EmployeeRes createEmployee(String uid, EmployeeReq request){
        valid(request);
        if(repo.existsByEmail(request.getEmail())){
            throw new EntityNotFoundException("email-used");
        }
        if(repo.existsByPhoneNumber(request.getPhoneNumber())){
            throw new EntityNotFoundException("phone-number-used");
        }

        Employee employee = Employee.of(uid, request);

        //create Account for new Employee
        Account account = new Account();
        account.setUsername(request.getEmail());
        account.setPassword(passwordEncoder.encode(Constant.PASSWORD_DEFAULT));
        account.setCreatedBy(uid);
        account.setUpdatedBy(uid);
        accountRepo.save(account);

        employee.setAccountId(account.getId());
        repo.save(employee);

        EmployeeRes employeeRes = employee.toRES();
        employeeRes.setUsername(account.getUsername());

        return employeeRes;
    }

    public Employee updateEmployee(String uid, Long id, EmployeeReq request){
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

    @Override
    public String deleteEmployee(Long id){
        if(employeeCenterRepo.existsByEmployeeId(id)){
            throw new EntityExistsException("employee-is-currently-in-the-center");
        }
        Employee employee = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + id));
        accountRepo.deleteById(employee.getAccountId());
        repo.delete(employee);

        return "Success!";
    }

    @Override
    public Map<String, Object> getById(Long id){
        var employee = repo.getEmployeeById(id).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + id));
        List<Map<String, Object>> employeeCenter = employeeCenterRepo.getByEmployeeId(id);
        Map<String, Object> result = new HashMap<>(employee);
        result.put("position", employeeCenter);
        return result;
    }

    @Override
    public List<Map<String, Object>> getAll(){
        return repo.getAll();
    }

    @Override
    public Page<Employee> searchEmployee(String name, String email, String position, String programingLanguage, Pageable pageable){
        return repo.searchEmployee(name, email, position, programingLanguage, pageable);
    }


}
