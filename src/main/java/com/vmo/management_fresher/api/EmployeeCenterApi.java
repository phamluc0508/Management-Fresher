package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vmo.management_fresher.dto.request.EmployeeCenterReq;
import com.vmo.management_fresher.service.EmployeeCenterService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employee-center")
@RequiredArgsConstructor
@Tag(name = "EmployeeCenter", description = "EmployeeCenter API ")
public class EmployeeCenterApi {
    private final EmployeeCenterService service;

    @PostMapping()
    @Operation(
            summary = "Create an EmployeeCenter",
            description = "Create a new EmployeeCenter record with the provided details",
            tags = {"EmployeeCenter"})
    protected ResponseEntity create(@RequestBody EmployeeCenterReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.create(uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Move an employee to a different center",
            description = "Update the EmployeeCenter record to move the employee to a different center",
            tags = {"EmployeeCenter"})
    protected ResponseEntity moveEmployee(
            @Parameter(description = "ID of the employee-center record to be updated", required = true)
                    @PathVariable("id")
                    Long id,
            @RequestBody EmployeeCenterReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.moveEmployee(uid, id, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an EmployeeCenter record",
            description = "Delete the EmployeeCenter record with the specified ID",
            tags = {"EmployeeCenter"})
    protected ResponseEntity delete(
            @Parameter(description = "ID of the EmployeeCenter record to be deleted", required = true)
                    @PathVariable("id")
                    Long id) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.removeEmployeeFromCenter(uid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
