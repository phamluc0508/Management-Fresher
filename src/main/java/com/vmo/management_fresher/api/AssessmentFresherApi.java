package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vmo.management_fresher.dto.request.PointProgrammingLanguageReq;
import com.vmo.management_fresher.service.AssessmentFresherService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/assessment-fresher")
@RequiredArgsConstructor
@Tag(name = "AssessmentFresher", description = "AssessmentFresher API ")
public class AssessmentFresherApi {
    private final AssessmentFresherService service;

    @PostMapping("/assessment-assign-fresher")
    @Operation(
            summary = "Assign assessment to Fresher",
            description = "Assign a specific assessment to a Fresher using their employee ID and assessment ID",
            tags = {"AssessmentFresher"})
    protected ResponseEntity addAssessmentFresher(
            @Parameter(description = "ID of the Fresher employee", required = true) @RequestParam Long employeeId,
            @Parameter(description = "ID of the assessment to assign", required = true) @RequestParam
                    Long assessmentId) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.addAssessmentFresher(uid, employeeId, assessmentId));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PatchMapping("/{id}/update-point-language")
    @Operation(
            summary = "Update programming languages, point",
            description = "Update the programming languages, point for a specified ID",
            tags = {"AssessmentFresher"})
    protected ResponseEntity updatePointLanguage(
            @Parameter(description = "ID of the Assessment Fresher to update", required = true) @PathVariable("id")
                    Long id,
            @RequestBody PointProgrammingLanguageReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updatePointAndProgrammingLanguage(uid, id, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Assessment Fresher",
            description = "Delete the Assessment Fresher associated with the specified Fresher ID."
                    + " Use the 'allow' parameter to confirm the deletion.",
            tags = {"AssessmentFresher"})
    protected ResponseEntity deleteAssessmentFresher(
            @Parameter(description = "ID of the assessment to be deleted", required = true) @PathVariable("id") Long id,
            @Parameter(description = "Flag to confirm deletion", example = "false")
                    @RequestParam(value = "allow", defaultValue = "false")
                    Boolean allow) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteAssessmentFresher(uid, id, allow));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/cal-average")
    @Operation(
            summary = "Calculate average score for Fresher",
            description = "Calculate the average score for a Fresher identified by their employee ID",
            tags = {"AssessmentFresher"})
    protected ResponseEntity calAverageFresher(
            @Parameter(description = "ID of the Fresher employee", required = true) @RequestParam Long employeeId) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.calAverageFresher(uid, employeeId));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
