package com.vmo.management_fresher.service;

import com.vmo.management_fresher.base.constant.Constant;
import com.vmo.management_fresher.dto.request.EmployeeCenterReq;
import com.vmo.management_fresher.model.Center;
import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.model.EmployeeCenter;
import com.vmo.management_fresher.model.Position;
import com.vmo.management_fresher.repository.CenterRepo;
import com.vmo.management_fresher.repository.EmployeeCenterRepo;
import com.vmo.management_fresher.repository.EmployeeRepo;
import com.vmo.management_fresher.repository.PositionRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeCenterService {
    private final EmployeeCenterRepo repo;
    private final EmployeeRepo employeeRepo;
    private final CenterRepo centerRepo;
    private final PositionRepo positionRepo;

    private void valid(EmployeeCenterReq request){
        if(request.getEmployeeId() == null){
            throw new RuntimeException("employeeId-by-cannot-be-null");
        } else {
            Employee employee = employeeRepo.findById(request.getEmployeeId()).orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + request.getEmployeeId()));
        }
        if(request.getCenterId() == null){
            throw new RuntimeException("centerId-by-cannot-be-null");
        } else {
            Center center = centerRepo.findById(request.getCenterId()).orElseThrow(() -> new EntityNotFoundException("center-not-found-with-id: " + request.getCenterId()));
        }
        if(StringUtils.isEmpty(request.getPosition())){
            throw new RuntimeException("position-by-cannot-be-null");
        }
    }

    public EmployeeCenter create(String uid, EmployeeCenterReq request){
        valid(request);
        if(request.getPosition().equals(Constant.FRESHER_POSITION)){
            if(repo.existsByEmployeeId(request.getEmployeeId())){
                throw new RuntimeException("employee-is-currently-in-the-center");
            }
        } else if(repo.existsByEmployeeIdAndPositionName(request.getEmployeeId(), Constant.FRESHER_POSITION)){
            throw new RuntimeException("employee-is-currently-fresher");
        } else if(repo.existsByCenterIdAndPositionName(request.getCenterId(), Constant.DIRECTOR_POSITION)){
            throw new RuntimeException("center-already-has-director");
        }

        Position position = positionRepo.findById(request.getPosition()).orElseThrow(() -> new EntityNotFoundException("position-not-found-with-name: " + request.getPosition()));

        EmployeeCenter employeeCenter = new EmployeeCenter();
        employeeCenter.setEmployeeId(request.getEmployeeId());
        employeeCenter.setCenterId(request.getCenterId());
        employeeCenter.setPosition(position);
        employeeCenter.setCreatedBy(uid);
        employeeCenter.setUpdatedBy(uid);

        return repo.save(employeeCenter);
    }

    public String removeEmployeeFromCenter(Long id){
        EmployeeCenter employeeCenter = repo.findById(id).orElseThrow(() -> new EntityNotFoundException("employeeCenter-not-found-with-id: " + id));
        repo.delete(employeeCenter);
        return "Success!";
    }
}
