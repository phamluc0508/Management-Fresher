package com.vmo.management_fresher.api;

import com.vmo.management_fresher.model.Account;
import com.vmo.management_fresher.service.AccountService;
import com.vmo.management_fresher.utility.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Account API ")
public class AccountApi {
    private final AccountService service;

    @PostMapping("/registration")
    protected ResponseEntity createAccount(
            @RequestHeader String uid,
            @RequestBody Account request
    ){
        try{
            return ResponseUtils.handlerSuccess(service.createAccount(uid, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    protected ResponseEntity updateAccount(
            @RequestHeader String uid,
            @PathVariable("id") String id,
            @RequestBody Account request
    ){
        try{
            return ResponseUtils.handlerSuccess(service.updateAccount(uid, id, request));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    protected ResponseEntity getById(
            @PathVariable("id") String id
    ){
        try {
            return ResponseUtils.handlerSuccess(service.getById(id));
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    protected ResponseEntity getAll(){
        try{
            return ResponseUtils.handlerSuccess(service.getAll());
        }catch (Exception ex){
            return ResponseUtils.handlerException(ex);
        }
    }
}
