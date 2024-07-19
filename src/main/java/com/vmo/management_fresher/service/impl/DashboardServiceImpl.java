package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.service.DashboardService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final EmployeeRepo repo;
    private final AuthenticationService authenticationService;
    private final EmployeeRepo employeeRepo;
    private final EmployeeCenterRepo employeeCenterRepo;

    @Override
    public Map<String, Object> numberFreshersCenter(String uid, Long centerId){
        if(!authenticationService.checkAdminRole(uid) && !authenticationService.checkDirectorCenter(uid, centerId)) {
            throw new AccessDeniedException("no-permission");
        }
        List<Employee> employees = repo.findEmployeesInCenter(centerId, Constant.FRESHER_POSITION);

        Map<String, Object> result = new HashMap<>();
        result.put("totalElements", employees.size());
        result.put("employees", employees);

        return result;
    }

    @Override
    public List<Map<String, Object>> findFreshersByPoint(String uid){
        List<Map<String, Object>> result = new ArrayList<>();
        if(authenticationService.checkAdminRole(uid)){
            result = repo.findEmployeesByPoint(Constant.FRESHER_POSITION);
        } else if (authenticationService.checkDirectorRole(uid)){
            Employee employee = employeeRepo.findByAccountId(uid).orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
            List<Long> centerIds = employeeCenterRepo.findCenterIdsByDirectorId(employee.getId(), Constant.DIRECTOR_POSITION);
            result = repo.findEmployeesByPointAndCenterIds(centerIds, Constant.FRESHER_POSITION);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> findFreshersByAVG(String uid){
        List<Map<String, Object>> result = new ArrayList<>();
        if(authenticationService.checkAdminRole(uid)){
            result = repo.findEmployeesByAVG(Constant.FRESHER_POSITION);
        } else if (authenticationService.checkDirectorRole(uid)){
            Employee employee = employeeRepo.findByAccountId(uid).orElseThrow(() -> new EntityNotFoundException("employee-not-found"));
            List<Long> centerIds = employeeCenterRepo.findCenterIdsByDirectorId(employee.getId(), Constant.DIRECTOR_POSITION);
            result = repo.findEmployeesByAVGAndCenterIds(centerIds, Constant.FRESHER_POSITION);
        }
        return result;
    }

}
