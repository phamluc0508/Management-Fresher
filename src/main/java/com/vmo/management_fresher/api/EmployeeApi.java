package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.EmployeeReq;
import com.vmo.management_fresher.service.EmployeeService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name = "Employee", description = "Employee API ")
public class EmployeeApi {
    private final EmployeeService service;

    @PostMapping()
    protected ResponseEntity createEmployee(
            @RequestBody EmployeeReq request
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createEmployee(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    protected ResponseEntity updateEmployee(
            @PathVariable("id") Long id,
            @RequestBody EmployeeReq request
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateEmployee(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    protected ResponseEntity deleteEmployee(
        @PathVariable("id") Long id
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteEmployee(id));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    protected ResponseEntity getById(
            @PathVariable("id") Long id
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(uid, id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    protected ResponseEntity getAll(){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/search")
    protected ResponseEntity searchEmployee(
            @RequestParam(value = "name", defaultValue = "#") String name,
            @RequestParam(value = "email", defaultValue = "#") String email,
            @RequestParam(value = "position", defaultValue = "#") String position,
            @RequestParam(value = "programmingLanguage", defaultValue = "#") String programmingLanguage,
            Pageable pageable
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.searchEmployee(uid, name, email, position, programmingLanguage, pageable));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
