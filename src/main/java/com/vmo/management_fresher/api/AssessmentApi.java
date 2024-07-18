package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.service.AssessmentService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
            @RequestParam("file") MultipartFile file,
            @RequestParam("assessmentType") Integer assessmentType,
            @RequestParam("centerId") Long centerId
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.storeFile(uid, file, assessmentType, centerId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/delete-file/{id}")
    protected ResponseEntity deleteFile(
            @PathVariable("id") Long id
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            service.deleteFile(uid, id);
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
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(uid, id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-by-centerId/{centerId}")
    protected ResponseEntity getAllByCenterId(
            @PathVariable("centerId") Long centerId
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAllByCenterId(uid, centerId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/download/{id}")
    protected ResponseEntity downloadFile(
            @PathVariable("id") Long id
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            Assessment assessment = service.getById(uid, id);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(assessment.getFileType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "assessment; filename=\"" + assessment.getFileName() + "\"")
                    .body(new ByteArrayResource(assessment.getFileContent()));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
