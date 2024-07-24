package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vmo.management_fresher.service.DashboardService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard API ")
public class DashboardApi {
    private final DashboardService dashboardService;

    @GetMapping("/number-freshers-by-center")
    @Operation(
            summary = "Get the number of Freshers by Center",
            description = "Retrieve the number of Freshers associated with the specified center",
            tags = {"Dashboard"})
    protected ResponseEntity numberFresherByCenter(
            @Parameter(description = "ID of the center to retrieve the number of Freshers for", required = true)
                    @RequestParam
                    Long centerId) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.numberFreshersCenter(uid, centerId));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/freshers-by-point")
    @Operation(
            summary = "Get Freshers by Point",
            description = "Retrieve a list of Freshers based on their points",
            tags = {"Dashboard"})
    protected ResponseEntity freshersByPoint() {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.findFreshersByPoint(uid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/freshers-by-avg")
    @Operation(
            summary = "Get Freshers by Average Score",
            description = "Retrieve a list of Freshers based on their average score",
            tags = {"Dashboard"})
    protected ResponseEntity freshersByAVG() {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.findFreshersByAVG(uid));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
