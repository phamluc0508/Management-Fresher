package com.vmo.management_fresher.service;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.dto.response.EmployeeRes;
import com.vmo.management_fresher.model.Employee;

public interface EmployeeService {
    EmployeeRes createEmployee(String uid, EmployeeReq request);

    Employee updateEmployee(String uid, Long id, EmployeeReq request);

    String deleteEmployee(Long id);

    Map<String, Object> getById(String uid, Long id);

    List<Map<String, Object>> getAll();

    Page<Employee> searchEmployee(
            String uid, String name, String email, String position, String programingLanguage, Pageable pageable);
}
