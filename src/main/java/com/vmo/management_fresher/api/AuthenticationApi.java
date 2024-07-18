package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication API ")
public class AuthenticationApi {
    private final AuthenticationService service;

    @PostMapping("/login")
    protected ResponseEntity login(
            @RequestBody AuthenticationReq request
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.login(request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/introspect")
    protected ResponseEntity introspect(
            @RequestParam String token
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.introspect(token));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/logout")
    protected ResponseEntity logout(
            @RequestParam String token
    ){
        try{
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.logout(uid, token));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PostMapping("/refresh-token")
    protected ResponseEntity refreshToken(
            @RequestParam String token
    ){
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.refreshToken(uid, token));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
