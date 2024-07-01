package com.vmo.management_fresher.api;

import com.vmo.management_fresher.service.AssessmentService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/assessment")
@RequiredArgsConstructor
@Tag(name = "Assessment", description = "Assessment API ")
public class AssessmentApi {
    private final AssessmentService service;

    @PostMapping("/upload-file")
    protected ResponseEntity storeFile(
            @RequestHeader String uid,
            @RequestParam("file") MultipartFile file,
            @RequestParam("typeAssessment") Integer typeAssessment,
            @RequestParam("centerId") Long centerId
            ){
        try{
            service.storeFile(uid, file, typeAssessment, centerId);
            return ResponseUtils.handlerSuccess();
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/delete-file/{id}")
    protected ResponseEntity deleteFile(
            @PathVariable("id") Long id
    ){
        try{
            service.deleteFile(id);
            return ResponseUtils.handlerSuccess();
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-by-id/{id}")
    protected ResponseEntity getById(
            @PathVariable("id") Long id
    ){
        try {
            return ResponseUtils.handlerSuccess(service.getById(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
