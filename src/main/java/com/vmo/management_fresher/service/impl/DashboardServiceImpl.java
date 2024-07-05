package com.vmo.management_fresher.service.impl;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {
    private final EmployeeRepo repo;

    @Override
    public Map<String, Object> numberFreshersCenter(Long centerId){
        List<Employee> employees = repo.findEmployeesInCenter(centerId, Constant.FRESHER_POSITION);

        Map<String, Object> result = new HashMap<>();
        result.put("totalElements", employees.size());
        result.put("employees", employees);

        return result;
    }

    @Override
    public List<Map<String, Object>> findFreshersByPoint(){
        return repo.findEmployeesByPoint(Constant.FRESHER_POSITION);
    }

    @Override
    public List<Map<String, Object>> findFreshersByAVG(){
        return repo.findEmployeesByAVG(Constant.FRESHER_POSITION);
    }

}
