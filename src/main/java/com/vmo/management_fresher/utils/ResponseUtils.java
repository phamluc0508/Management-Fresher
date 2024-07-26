package com.vmo.management_fresher.utils;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;

import com.vmo.management_fresher.exception.ExpectationFailedException;
import org.springframework.stereotype.Component;

import java.util.Locale;


@Component
public class ResponseUtils {

    private static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        ResponseUtils.messageSource = messageSource;
    }

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
        } else if (ex instanceof EntityExistsException || ex instanceof DataIntegrityViolationException) {
            httpStatus = HttpStatus.CONFLICT;
        } else if (ex instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;
        } else if (ex instanceof InsufficientAuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
        } else if (ex instanceof ExpectationFailedException) {
            httpStatus = HttpStatus.EXPECTATION_FAILED;
        } else if (ex instanceof RuntimeException) {
            httpStatus = HttpStatus.BAD_REQUEST;
        } else {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String message = ex.getMessage();
        Locale locale = LocaleContextHolder.getLocale();
        try {
            message = messageSource.getMessage(message, null, locale);
        } catch (NoSuchMessageException e){

        }

        ApiResponse<Object> response = new ApiResponse<>();
        response.setCode(httpStatus.value());
        response.setMessage(message);
        response.setStatus("ERROR");
        return ResponseEntity.status(httpStatus).body(response);
    }
}
