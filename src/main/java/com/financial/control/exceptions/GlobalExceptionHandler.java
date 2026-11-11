package com.financial.control.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


@ExceptionHandler
public ResponseEntity<Object> handleInvalidCategory(InvalidCategoryException ex) {
    return buildResponseEntity(ex.getMessage(), HttpStatus.BAD_REQUEST);
}

@ExceptionHandler
public ResponseEntity<Object> handleDuplicateCategory(CategoryAlreadyExistsException ex) {
    return buildResponseEntity(ex.getMessage(), HttpStatus.CONFLICT);
}

@ExceptionHandler
public ResponseEntity<Object> handleCategoryNotFound(CategoryNotFoundException ex) {
    return buildResponseEntity(ex.getMessage(), HttpStatus.NOT_FOUND);
}

    @ExceptionHandler
    public ResponseEntity<Object> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildResponseEntity(message, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> buildResponseEntity(String message, HttpStatus status) {
       Map<String,Object> map = new HashMap<>();
       map.put("message", message);
       map.put("status", status.value());

       return new ResponseEntity<>(map, status);
    }
}
