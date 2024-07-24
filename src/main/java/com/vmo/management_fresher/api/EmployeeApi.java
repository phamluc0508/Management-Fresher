package com.vmo.management_fresher.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.service.EmployeeService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee API ")
public class EmployeeApi {
    private final EmployeeService service;

    @PostMapping()
    @Operation(
            summary = "Create a new Employee",
            description = "Create a new employee with the provided details",
            tags = {"Employee"})
    protected ResponseEntity createEmployee(@RequestBody EmployeeReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createEmployee(uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an Employee by ID",
            description = "Update the details of an existing employee identified by ID",
            tags = {"Employee"})
    protected ResponseEntity updateEmployee(
            @Parameter(description = "ID of the employee to be updated", required = true) @PathVariable("id") Long id,
            @RequestBody EmployeeReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateEmployee(uid, id, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete an Employee by ID",
            description = "Delete an existing employee identified by ID",
            tags = {"Employee"})
    protected ResponseEntity deleteEmployee(
            @Parameter(description = "ID of the employee to be deleted", required = true) @PathVariable("id") Long id) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteEmployee(id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Employee by ID",
            description = "Retrieve the details of an employee identified by ID",
            tags = {"Employee"})
    protected ResponseEntity getById(
            @Parameter(description = "ID of the employee to retrieve", required = true) @PathVariable("id") Long id) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(uid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    @Operation(
            summary = "Retrieve all employees",
            description = "Fetch and return a list of all employees",
            tags = {"Employee"})
    protected ResponseEntity getAll() {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search employees",
            description =
                    "Search for employees based on various criteria such as name, email, position, and programming language",
            tags = {"Employee"})
    protected ResponseEntity searchEmployee(
            @Parameter(description = "Name of the employee to search for", example = "Nguyen Van A")
                    @RequestParam(value = "name", defaultValue = "#")
                    String name,
            @Parameter(description = "Email of the employee to search for", example = "nguyenvana@email.com")
                    @RequestParam(value = "email", defaultValue = "#")
                    String email,
            @Parameter(description = "Position of the employee to search for", example = "FRESHER")
                    @RequestParam(value = "position", defaultValue = "#")
                    String position,
            @Parameter(description = "Programming language of the employee to search for", example = "JAVA")
                    @RequestParam(value = "programmingLanguage", defaultValue = "#")
                    String programmingLanguage,
            @Parameter(description = "Pagination information") Pageable pageable) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(
                    service.searchEmployee(uid, name, email, position, programmingLanguage, pageable));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
