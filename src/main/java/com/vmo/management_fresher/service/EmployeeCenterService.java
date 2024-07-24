package com.vmo.management_fresher.service;

import com.vmo.management_fresher.dto.request.EmployeeCenterReq;
import com.vmo.management_fresher.model.EmployeeCenter;

public interface EmployeeCenterService {
    EmployeeCenter create(String uid, EmployeeCenterReq request);

    String removeEmployeeFromCenter(String uid, Long id);

    EmployeeCenter moveEmployee(String uid, Long id, EmployeeCenterReq request);
}
