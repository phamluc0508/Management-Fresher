package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.service.RoleService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@Tag(name = "Role", description = "Role API ")
public class RoleApi {
    private final RoleService service;

    @PostMapping()
    protected ResponseEntity createRole(
            @RequestHeader String uid,
            @RequestBody Role request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.createRole(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{name}")
    protected ResponseEntity updateRole(
            @RequestHeader String uid,
            @PathVariable("name") String name,
            @RequestBody Role request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.updateRole(uid, name, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{name}")
    protected ResponseEntity deletePermission(
            @PathVariable("name") String name
    ){
        try{
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
            return ResponseUtils.handlerSuccess(service.getById(name));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    protected ResponseEntity getAll(){
        try {
            return ResponseUtils.handlerSuccess(service.getAll());
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
