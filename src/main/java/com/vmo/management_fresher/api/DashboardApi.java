package com.vmo.management_fresher.api;

import com.vmo.management_fresher.service.DashboardService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard", description = "Dashboard API ")
public class DashboardApi {
    private final DashboardService dashboardService;

    @GetMapping("/number-freshers-by-center")
    protected ResponseEntity numberFresherByCenter(
            @RequestParam Long centerId
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.numberFreshersCenter(uid, centerId));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/freshers-by-point")
    protected ResponseEntity freshersByPoint(
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.findFreshersByPoint(uid));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/freshers-by-avg")
    protected ResponseEntity freshersByAVG(
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(dashboardService.findFreshersByAVG(uid));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
