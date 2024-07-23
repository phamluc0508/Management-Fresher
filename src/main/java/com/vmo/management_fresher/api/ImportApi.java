package com.vmo.management_fresher.api;

import com.vmo.management_fresher.service.ImportService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
@Tag(name = "Import", description = "Import API ")
public class ImportApi {
    private final ImportService service;

    @PostMapping("/fresher-to-center")
    @Operation(
            summary = "Import Freshers to a Center from a excel file",
            description = "Upload a excel file to import fresher data into a specified center." +
                    " The file should contain details of freshers to be added to the center.",
            tags = {"Import"}
    )
    protected ResponseEntity importFresherToCenter(
            @Parameter(description = "The excel file containing fresher data to be imported", required = true)
            @RequestParam("file") MultipartFile file
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            Map<String, Object> errorFile = service.importFresherToCenter(uid, file);
            if(errorFile == null){
                return ResponseUtils.handlerSuccess("Success");
            } else {
                return ResponseUtils.handlerSuccess(errorFile);
            }
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/download-error-file/{fileName}")
    @Operation(
            summary = "Download error excel file",
            description = "Download a excel file containing error details for the import process." +
                    " The file name should be provided as a path variable.",
            tags = {"Import"}
    )
    protected ResponseEntity downloadErrorFile(
            @Parameter(description = "The name of the error file to be downloaded", required = true)
            @PathVariable String fileName
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            File file = new File(System.getProperty("java.io.tmpdir") + "/" + fileName);
            byte[] fileContent = new FileInputStream(file).readAllBytes();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
