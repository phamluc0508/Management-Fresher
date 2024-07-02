package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Employee;
import com.vmo.management_fresher.service.EmployeeService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee API ")
public class EmployeeApi {
    private final EmployeeService service;

    @PostMapping()
    protected ResponseEntity createEmployee(
            @RequestHeader String uid,
            @RequestBody Employee request
    ){
        try{
            return ResponseUtils.handlerSuccess(service.createEmployee(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    protected ResponseEntity updateEmployee(
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody Employee request
    ){
        try{
            return ResponseUtils.handlerSuccess(service.updateEmployee(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    protected ResponseEntity getById(
            @PathVariable("id") Long id
    ){
        try{
            return ResponseUtils.handlerSuccess(service.getById(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    protected ResponseEntity getAll(){
        try{
            return ResponseUtils.handlerSuccess(service.getAll());
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
