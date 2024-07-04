package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.service.AssessmentFresherService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessment-fresher")
@RequiredArgsConstructor
@Tag(name = "AssessmentFresher", description = "AssessmentFresher API ")
public class AssessmentFresherApi {
    private final AssessmentFresherService service;

    @PostMapping("/add-assessment-fresher")
    protected ResponseEntity addAssessmentFresher(
            @RequestHeader String uid,
            @RequestParam Long employeeId,
            @RequestParam Long assessmentId
    ){
        try{
            return ResponseUtils.handlerSuccess(service.addAssessmentFresher(uid, employeeId, assessmentId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PatchMapping("/{id}/update-point-language")
    protected ResponseEntity updatePointLanguage(
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody PointProgrammingLanguageReq request
    ){
        try{
            return ResponseUtils.handlerSuccess(service.updatePointAndProgrammingLanguage(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    protected ResponseEntity deleteAssessmentFresher(
            @PathVariable("id") Long id,
            @RequestParam(value = "allow", defaultValue = "false") Boolean allow
    ){
        try {
            return ResponseUtils.handlerSuccess(service.deleteAssessmentFresher(id, allow));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
