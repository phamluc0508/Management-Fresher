package com.vmo.management_fresher.api;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.service.AuthenticationService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            return ResponseUtils.handlerSuccess(service.introspect(token));
        } catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
