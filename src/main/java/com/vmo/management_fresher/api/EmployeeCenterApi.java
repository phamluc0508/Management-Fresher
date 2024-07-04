package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.EmployeeCenterReq;
import com.vmo.management_fresher.service.EmployeeCenterService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employee-center")
@RequiredArgsConstructor
@Tag(name = "EmployeeCenter", description = "EmployeeCenter API ")
public class EmployeeCenterApi {
    private final EmployeeCenterService service;

    @PostMapping()
    protected ResponseEntity create(
            @RequestHeader String uid,
            @RequestBody EmployeeCenterReq request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.create(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    protected ResponseEntity delete(
            @PathVariable("id") Long id
    ){
        try {
            return ResponseUtils.handlerSuccess(service.removeEmployeeFromCenter(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
