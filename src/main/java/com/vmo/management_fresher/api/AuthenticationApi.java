package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.utils.ResponseUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
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
    @Operation(
            summary = "User login",
            description = "Authenticate user and return a token if credentials are valid",
            tags = {"Authentication"}
    )
    @SecurityRequirements
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
    @Operation(
            summary = "Introspect token",
            description = "Retrieve information about the provided token to check its validity",
            tags = {"Authentication"}
    )
    protected ResponseEntity introspect(
            @Parameter(description = "Token to be introspected", required = true)
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
    @Operation(
            summary = "Logout user",
            description = "Invalidate the provided token to log out the user",
            tags = {"Authentication"}
    )
    protected ResponseEntity logout(
            @Parameter(description = "Token to be invalidated", required = true)
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
    @Operation(
            summary = "Refresh authentication token",
            description = "Generate a new authentication token using the provided refresh token",
            tags = {"Authentication"}
    )
    protected ResponseEntity refreshToken(
            @Parameter(description = "Refresh token to generate a new authentication token", required = true)
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
