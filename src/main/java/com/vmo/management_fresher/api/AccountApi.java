package com.vmo.management_fresher.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.vmo.management_fresher.dto.request.AuthenticationReq;
import com.vmo.management_fresher.service.AccountService;
import com.vmo.management_fresher.utils.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
@Tag(name = "Account", description = "Account API ")
public class AccountApi {
    private final AccountService service;

    @PostMapping("/registration")
    @Operation(
            summary = "Create new account",
            description = "Register a new account",
            tags = {"Account"})
    protected ResponseEntity createAccount(@RequestBody AuthenticationReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.createAccount(uid, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update account",
            description = "Update an existing account",
            tags = {"Account"})
    protected ResponseEntity updateAccount(
            @Parameter(description = "ID of the account to be updated", required = true) @PathVariable("id") String id,
            @RequestBody AuthenticationReq request) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.updateAccount(uid, id, request));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete account",
            description = "Delete the account with the specified ID",
            tags = {"Account"})
    protected ResponseEntity deleteAccount(
            @Parameter(description = "ID of the account to be deleted", required = true) @PathVariable("id")
                    String id) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.deleteAccount(id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @PatchMapping("/{id}/add-role-account")
    @Operation(
            summary = "Add role to account",
            description = "Add a role to the account with the specified ID",
            tags = {"Account"})
    protected ResponseEntity addRoleAccount(
            @Parameter(description = "ID of the account to which the role will be added", required = true)
                    @PathVariable("id")
                    String id,
            @RequestBody String role) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.addRoleAccount(uid, id, role));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get account by ID",
            description = "Retrieve the account details for the specified ID",
            tags = {"Account"})
    protected ResponseEntity getById(
            @Parameter(description = "ID of the account to retrieve", required = true) @PathVariable("id") String id) {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getById(uid, id));
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }

    @GetMapping("/get-all")
    @Operation(
            summary = "Get all accounts",
            description = "Retrieve a list of all accounts",
            tags = {"Account"})
    protected ResponseEntity getAll() {
        try {
            var context = SecurityContextHolder.getContext();
            String uid = context.getAuthentication().getName();

            return ResponseUtils.handlerSuccess(service.getAll());
        } catch (Exception ex) {
            return ResponseUtils.handlerException(ex);
        }
    }
}
