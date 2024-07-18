package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.service.AssessmentFresherService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/assessment-fresher")
@RequiredArgsConstructor
@Tag(name = "AssessmentFresher", description = "AssessmentFresher API ")
public class AssessmentFresherApi {
    private final AssessmentFresherService service;

    @PostMapping("/assessment-assign-fresher")
    protected ResponseEntity addAssessmentFresher(
            @RequestParam Long employeeId,
            @RequestParam Long assessmentId
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.addAssessmentFresher(uid, employeeId, assessmentId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PatchMapping("/{id}/update-point-language")
    protected ResponseEntity updatePointLanguage(
            @PathVariable("id") Long id,
            @RequestBody PointProgrammingLanguageReq request
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

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
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteAssessmentFresher(uid, id, allow));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/cal-average")
    protected ResponseEntity calAverageFresher(
            @RequestParam Long employeeId
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.calAverageFresher(uid, employeeId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
