package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Assessment;
import com.vmo.management_fresher.service.AssessmentService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
    @Operation(
            summary = "Upload assessment file",
            description = "Upload a file for a center",
            tags = {"Assessment"}
    )
    protected ResponseEntity storeFile(
            @Parameter(description = "File to be uploaded", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Type of the assessment", required = true)
            @RequestParam("assessmentType") Integer assessmentType,
            @Parameter(description = "ID of the center associated with the assessment", required = true)
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
    @Operation(
            summary = "Delete file",
            description = "Delete the assessment with the specified ID",
            tags = {"Assessment"}
    )
    protected ResponseEntity deleteFile(
            @Parameter(description = "ID of the assessment to be deleted", required = true)
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
    @Operation(
            summary = "Get item by ID",
            description = "Retrieve the details of assessment with the specified ID",
            tags = {"Assessment"}
    )
    protected ResponseEntity getById(
            @Parameter(description = "ID of the assessment to retrieve", required = true)
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
    @Operation(
            summary = "Get assessment by Center ID",
            description = "Retrieve all assessments associated with the specified Center ID",
            tags = {"Assessment"}
    )
    protected ResponseEntity getAllByCenterId(
            @Parameter(description = "ID of the center to retrieve assessments for", required = true)
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
    @Operation(
            summary = "Download assessment file by ID",
            description = "Download the assessment file associated with the specified ID",
            tags = {"Assessment"}
    )
    protected ResponseEntity downloadFile(
            @Parameter(description = "ID of the assessment file to be downloaded", required = true)
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
