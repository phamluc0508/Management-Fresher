package com.vmo.management_fresher.utility;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

public class ResponseUtils {
    public static <T> ResponseEntity<ApiResponse<T>> handlerSuccess(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setData(data);
        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Void>> handlerSuccess() {
        ApiResponse<Void> response = new ApiResponse<>();
        response.setStatus("SUCCESS");
        response.setCode(HttpStatus.OK.value());
        return ResponseEntity.ok(response);
    }

    public static ResponseEntity<ApiResponse<Object>> handlerException(Exception ex) {
        HttpStatus httpStatus;
        if (ex instanceof EntityNotFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;
        } else if (ex instanceof EntityExistsException) {
            httpStatus = HttpStatus.CONFLICT;
        } else if (ex instanceof AccessDeniedException){
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (ex instanceof InsufficientAuthenticationException){
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof RuntimeException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(httpStatus.value());
        response.setMessage(ex.getMessage());
        response.setStatus("ERROR");
        return ResponseEntity.status(httpStatus).body(response);
    }
}
