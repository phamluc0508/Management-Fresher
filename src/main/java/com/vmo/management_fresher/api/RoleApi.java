package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Role;
import com.vmo.management_fresher.service.RoleService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "Create a new role",
            description = "Create a new role with the specified details." +
                    " Requires authentication.",
            tags = {"Role"}
    )
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
    @Operation(
            summary = "Update an existing role",
            description = "Update the details of an existing role identified by its name." +
                    " Requires authentication.",
            tags = {"Role"}
    )
    protected ResponseEntity updateRole(
            @Parameter(description = "Name of the role to be updated", required = true)
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
    @Operation(
            summary = "Delete a role",
            description = "Delete an existing role identified by its name." +
                    " Requires authentication.",
            tags = {"Role"}
    )
    protected ResponseEntity deletePosition(
            @Parameter(description = "Name of the role to be deleted", required = true)
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
    @Operation(
            summary = "Retrieve a role by name",
            description = "Retrieve the details of a role identified by its name.",
            tags = {"Role"}
    )
    protected ResponseEntity getById(
            @Parameter(description = "Name of the role to be retrieved", required = true)
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
    @Operation(
            summary = "Retrieve all roles",
            description = "Retrieve a list of all roles.",
            tags = {"Role"}
    )
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
