package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.ProgrammingLanguage;
import com.vmo.management_fresher.service.ProgrammingLanguageService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programming-language")
@RequiredArgsConstructor
@Tag(name = "ProgrammingLanguage", description = "ProgrammingLanguage API ")
public class ProgrammingLanguageApi {
    private final ProgrammingLanguageService service;

    @PostMapping()
    @Operation(
            summary = "Create a new programming language",
            description = "Create a new programming language." +
                    " Requires authentication.",
            tags = {"ProgrammingLanguage"}
    )
    protected ResponseEntity createPosition(
            @RequestBody ProgrammingLanguage request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createProgrammingLanguage(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{name}")
    @Operation(
            summary = "Update an existing programming language",
            description = "Update an existing programming language identified by its name." +
                    " Requires authentication.",
            tags = {"ProgrammingLanguage"}
    )
    protected ResponseEntity updatePosition(
            @Parameter(description = "Name of the programming language to be updated", required = true)
            @PathVariable("name") String name,
            @RequestBody ProgrammingLanguage request
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateProgrammingLanguage(uid, name, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{name}")
    @Operation(
            summary = "Delete a programming language",
            description = "Delete a programming language identified by its name." +
                    " Requires authentication.",
            tags = {"ProgrammingLanguage"}
    )
    protected ResponseEntity deletePosition(
            @Parameter(description = "Name of the programming language to be deleted", required = true)
            @PathVariable("name") String name
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteProgrammingLanguage(name));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{name}")
    @Operation(
            summary = "Retrieve a programming language by name",
            description = "Retrieve the details of a programming language identified by its name.",
            tags = {"ProgrammingLanguage"}
    )
    protected ResponseEntity getById(
            @Parameter(description = "Name of the programming language to be retrieved", required = true)
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
            summary = "Retrieve all programming languages",
            description = "Retrieve a list of all programming languages.",
            tags = {"ProgrammingLanguage"}
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
