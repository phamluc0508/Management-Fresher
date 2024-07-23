package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.GroupCenterReq;
import com.vmo.management_fresher.model.Center;
import com.vmo.management_fresher.service.CenterService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/center")
@RequiredArgsConstructor
@Tag(name = "Center", description = "Center API ")
public class CenterApi {
    private final CenterService service;

    @PostMapping()
    @Operation(
            summary = "Create a single Center",
            description = "Create a new center",
            tags = {"Center"}
    )
    protected ResponseEntity createCenter(
            @RequestBody Center request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createCenter(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Center by Id",
            description = "Update a single center using the provided ID and center details",
            tags = {"Center"}
    )
    protected ResponseEntity updateCenter(
            @Parameter(description = "ID of the center to be updated", required = true)
            @PathVariable("id") Long id,
            @RequestBody Center request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateCenter(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Center by Id",
            description = "Delete a single center using the provided ID",
            tags = {"Center"}
    )
    protected ResponseEntity deleteCenter(
            @Parameter(description = "ID of the center to be deleted", required = true)
            @PathVariable("id") Long id
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteCenter(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "View Center by Id",
            description = "Retrieve details of a single center using the provided ID",
            tags = {"Center"}
    )
    protected ResponseEntity getById(
            @Parameter(description = "ID of the center to be retrieved", required = true)
            @PathVariable("id") Long id
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(uid, id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    @Operation(
            summary = "View all Centers",
            description = "Retrieve details of all centers",
            tags = {"Center"}
    )
    protected ResponseEntity getAll(){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/search")
    @Operation(
            summary = "Search all Centers by name",
            description = "Search for all centers matching the provided name, with pagination support",
            tags = {"Center"}
    )
    protected ResponseEntity search(
            @Parameter(description = "Name or part of the name to search for", example = "Central", required = true)
            @RequestParam(value = "search", defaultValue = "#") String search,

            @Parameter(description = "Pagination information", required = true)
            Pageable pageable
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.search(search, pageable));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/group-two-center")
    @Operation(
            summary = "Group two Centers",
            description = "Group two centers into a single group using the provided information",
            tags = {"Center"}
    )
    protected ResponseEntity groupTwoCenter(
            @Parameter(description = "Request object containing details of the two centers to be grouped", required = true)
            @RequestBody GroupCenterReq request
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.groupTwoCenter(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
