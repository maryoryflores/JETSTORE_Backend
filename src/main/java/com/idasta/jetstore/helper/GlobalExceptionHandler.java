package com.idasta.jetstore.helper;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handlerRuntime(RuntimeException ex){
        return ResponseEntity
                .badRequest()
                .body(new RespuestaApi(false, ex.getMessage(), null));
    }
}
