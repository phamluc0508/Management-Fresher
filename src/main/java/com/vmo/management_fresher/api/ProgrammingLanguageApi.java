package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.ProgrammingLanguage;
import com.vmo.management_fresher.service.ProgrammingLanguageService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/programming-language")
@RequiredArgsConstructor
@Tag(name = "ProgrammingLanguage", description = "ProgrammingLanguage API ")
public class ProgrammingLanguageApi {
    private final ProgrammingLanguageService service;

    @PostMapping()
    protected ResponseEntity createPosition(
            @RequestHeader String uid,
            @RequestBody ProgrammingLanguage request
    ){
        try {
            return ResponseUtils.handlerSuccess(service.createProgrammingLanguage(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{name}")
    protected ResponseEntity updatePosition(
            @RequestHeader String uid,
            @PathVariable("name") String name,
            @RequestBody ProgrammingLanguage request
    ){
        try {
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
