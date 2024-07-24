package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vmo.management_fresher.model.Position;
import com.vmo.management_fresher.service.PositionService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/position")
@RequiredArgsConstructor
@Tag(name = "Position", description = "Position API ")
public class PositionApi {
    private final PositionService service;

    @PostMapping()
    @Operation(
            summary = "Create a new position",
            description =
                    "Create a new position with the specified details. The position information should be provided in the request body",
            tags = {"Position"})
    protected ResponseEntity createPosition(@RequestBody Position request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createPosition(uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{name}")
    @Operation(
            summary = "Update an existing position",
            description = "Update the details of an existing position identified by the position name."
                    + " The updated position information should be provided in the request body.",
            tags = {"Position"})
    protected ResponseEntity updatePosition(
            @Parameter(description = "The name of the position to be updated", required = true) @PathVariable("name")
                    String name,
            @RequestBody Position request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updatePosition(uid, name, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{name}")
    @Operation(
            summary = "Delete a position by name",
            description = "Delete an existing position identified by the position name."
                    + " The position name should be provided as a path variable.",
            tags = {"Position"})
    protected ResponseEntity deletePosition(
            @Parameter(description = "The name of the position to be deleted", required = true) @PathVariable("name")
                    String name) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deletePosition(name));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{name}")
    @Operation(
            summary = "Get position by name",
            description = "Retrieve the details of a position identified by the position name"
                    + " The position name should be provided as a path variable.",
            tags = {"Position"})
    protected ResponseEntity getById(
            @Parameter(description = "The name of the position to be retrieved", required = true) @PathVariable("name")
                    String name) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(name));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    @Operation(
            summary = "Get all positions",
            description = "Retrieve a list of all positions",
            tags = {"Position"})
    protected ResponseEntity getAll() {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
