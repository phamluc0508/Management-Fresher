package com.vmo.management_fresher.repository;

import com.vmo.management_fresher.model.EmployeeCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeCenterRepo extends JpaRepository<EmployeeCenter, Long> {
    Boolean existsByEmployeeId(Long employeeId);
    Boolean existsByEmployeeIdAndPositionName(Long employeeId, String positionName);
    Boolean existsByCenterId(Long centerId);
}
