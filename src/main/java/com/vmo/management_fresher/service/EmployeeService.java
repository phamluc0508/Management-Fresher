package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface EmployeeService {
    EmployeeRes createEmployee(String uid, EmployeeReq request);
    Employee updateEmployee(String uid, Long id, EmployeeReq request);
    String deleteEmployee(Long id);
    Map<String, Object> getById(String uid, Long id);
    List<Map<String, Object>> getAll();
    Page<Employee> searchEmployee(String uid, String name, String email, String position, String programingLanguage, Pageable pageable);
}
