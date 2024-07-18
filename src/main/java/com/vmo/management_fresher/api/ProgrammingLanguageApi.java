package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.ProgrammingLanguage;
import com.vmo.management_fresher.service.ProgrammingLanguageService;
import com.vmo.management_fresher.utils.ResponseUtils;
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
    protected ResponseEntity updatePosition(
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
    protected ResponseEntity deletePosition(
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
