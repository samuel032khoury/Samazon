package com.samazon.application.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.samazon.application.dto.common.APIResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            response.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String paramName = ex.getName();
        Class<?> requiredTypeClass = ex.getRequiredType();
        String requiredType = requiredTypeClass != null ? requiredTypeClass.getSimpleName() : "unknown";

        String message = String.format(
                "Parameter '%s' should be of type %s, but value '%s' could not be converted.",
                paramName, requiredType, ex.getValue());

        APIResponse apiResponse = new APIResponse(message, false);

        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(404).body(apiResponse);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<APIResponse> handleUnauthorizedException(UnauthorizedException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(401).body(apiResponse);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse> handleAccessDeniedException(AccessDeniedException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(403).body(apiResponse);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleAPIException(APIException ex) {
        APIResponse apiResponse = new APIResponse(ex.getMessage(), false);
        return ResponseEntity.status(400).body(apiResponse);
    }
}
