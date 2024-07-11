package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.EmployeeCenterReq;
import com.vmo.management_fresher.service.EmployeeCenterService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee-center")
@RequiredArgsConstructor
@Tag(name = "EmployeeCenter", description = "EmployeeCenter API ")
public class EmployeeCenterApi {
    private final EmployeeCenterService service;

    @PostMapping()
    protected ResponseEntity create(
            @RequestBody EmployeeCenterReq request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.create(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    protected ResponseEntity create(
            @PathVariable("id") Long id,
            @RequestBody EmployeeCenterReq request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.moveEmployee(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    protected ResponseEntity delete(
            @PathVariable("id") Long id
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.removeEmployeeFromCenter(uid, id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
