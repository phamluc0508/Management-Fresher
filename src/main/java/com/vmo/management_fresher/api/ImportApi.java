package com.vmo.management_fresher.api;

import com.vmo.management_fresher.service.ImportService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    protected ResponseEntity importFresherToCenter(
            @RequestHeader String uid,
            @RequestParam("file") MultipartFile file
    ){
        try{
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
    protected ResponseEntity downloadErrorFile(
            @PathVariable String fileName
    ){
        try{
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
