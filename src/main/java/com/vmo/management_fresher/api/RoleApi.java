package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.service.RoleService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Role API ")
public class RoleApi {
    private final RoleService service;

    @PostMapping()
    protected ResponseEntity createRole(
            @RequestBody Role request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createRole(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{name}")
    protected ResponseEntity updateRole(
            @PathVariable("name") String name,
            @RequestBody Role request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateRole(uid, name, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{name}")
    protected ResponseEntity deletePosition(
            @PathVariable("name") String name
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteRole(name));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{name}")
    protected ResponseEntity getById(
            @PathVariable("name") String name
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(name));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    protected ResponseEntity getAll(){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
