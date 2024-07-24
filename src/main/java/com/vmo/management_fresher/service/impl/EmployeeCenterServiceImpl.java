package com.vmo.management_fresher.service.impl;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
import com.vmo.management_fresher.service.AccountService;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.service.EmployeeCenterService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeCenterServiceImpl implements EmployeeCenterService {
    private final EmployeeCenterRepo repo;
    private final EmployeeRepo employeeRepo;
    private final CenterRepo centerRepo;
    private final PositionRepo positionRepo;
    private final AccountService accountService;
    private final AuthenticationService authenticationService;

    private void valid(EmployeeCenterReq request) {
        if (request.getEmployeeId() == null) {
            throw new RuntimeException("employeeId-by-cannot-be-null");
        } else {
            Employee employee = employeeRepo
                    .findById(request.getEmployeeId())
                    .orElseThrow(() ->
                            new EntityNotFoundException("employee-not-found-with-id: " + request.getEmployeeId()));
        }
        if (request.getCenterId() == null) {
            throw new RuntimeException("centerId-by-cannot-be-null");
        } else {
            Center center = centerRepo
                    .findById(request.getCenterId())
                    .orElseThrow(
                            () -> new EntityNotFoundException("center-not-found-with-id: " + request.getCenterId()));
        }
        if (StringUtils.isEmpty(request.getPosition())) {
            throw new RuntimeException("position-by-cannot-be-null");
        }
    }

    private void updateRoleOfAccount(String uid, Long employeeId, String position) {
        Employee employee = employeeRepo
                .findById(employeeId)
                .orElseThrow(() -> new EntityNotFoundException("employee-not-found-with-id: " + employeeId));
        if (position.equals(Constant.FRESHER_POSITION)) {
            accountService.addRoleAccount(uid, employee.getAccountId(), Constant.FRESHER_ROLE);
        } else if (position.equals(Constant.DIRECTOR_POSITION)) {
            accountService.addRoleAccount(uid, employee.getAccountId(), Constant.DIRECTOR_ROLE);
        } else if (position.equals(Constant.OTHER_POSITION)) {
            accountService.addRoleAccount(uid, employee.getAccountId(), Constant.OTHER_ROLE);
        }
    }

    @Override
    public EmployeeCenter create(String uid, EmployeeCenterReq request) {
        valid(request);
        Position position = positionRepo
                .findById(request.getPosition())
                .orElseThrow(
                        () -> new EntityNotFoundException("position-not-found-with-name: " + request.getPosition()));
        if (request.getPosition().equals(Constant.FRESHER_POSITION)) {
            if (repo.existsByEmployeeId(request.getEmployeeId())) {
                throw new RuntimeException("employee-is-currently-in-the-center");
            }
        } else if (repo.existsByEmployeeIdAndPositionName(request.getEmployeeId(), Constant.FRESHER_POSITION)) {
            throw new RuntimeException("employee-is-currently-fresher");
        } else if (repo.existsByEmployeeIdAndCenterIdAndPositionName(
                request.getEmployeeId(), request.getCenterId(), Constant.DIRECTOR_POSITION)) {
            throw new EntityExistsException("employee-is-currently-director-in-the-center");
        }

        EmployeeCenter employeeCenter = new EmployeeCenter();
        employeeCenter.setEmployeeId(request.getEmployeeId());
        employeeCenter.setCenterId(request.getCenterId());
        employeeCenter.setPosition(position);
        employeeCenter.setCreatedBy(uid);
        employeeCenter.setUpdatedBy(uid);

        employeeCenter = repo.save(employeeCenter);

        updateRoleOfAccount(uid, request.getEmployeeId(), request.getPosition());

        return employeeCenter;
    }

    @Override
    public EmployeeCenter moveEmployee(String uid, Long id, EmployeeCenterReq request) {
        valid(request);

        Position position = positionRepo
                .findById(request.getPosition())
                .orElseThrow(
                        () -> new EntityNotFoundException("position-not-found-with-name: " + request.getPosition()));
        if (request.getPosition().equals(Constant.FRESHER_POSITION)) {
            if (repo.existsByEmployeeIdAndIdIsNot(request.getEmployeeId(), id)) {
                throw new RuntimeException("employee-cannot-be-fresher");
            }
        }

        EmployeeCenter employeeCenter = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("employee-center-not-found-with-id: " + id));

        employeeCenter.setEmployeeId(request.getEmployeeId());
        employeeCenter.setCenterId(request.getCenterId());
        employeeCenter.setPosition(position);
        employeeCenter.setUpdatedBy(uid);

        employeeCenter = repo.save(employeeCenter);

        updateRoleOfAccount(uid, request.getEmployeeId(), request.getPosition());

        return employeeCenter;
    }

    @Override
    public String removeEmployeeFromCenter(String uid, Long id) {
        EmployeeCenter employeeCenter = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("employeeCenter-not-found-with-id: " + id));

        if (!authenticationService.checkAdminRole(uid)
                && !authenticationService.checkDirectorFresher(uid, employeeCenter.getEmployeeId())) {
            throw new AccessDeniedException("no-permission");
        }

        repo.delete(employeeCenter);

        updateRoleOfAccount(uid, employeeCenter.getEmployeeId(), Constant.OTHER_POSITION);

        return "Success!";
    }
}
