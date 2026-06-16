package com.mfpe.adapter.in.rest;

import com.mfpe.exception.OrderAlreadyCancelledException;
import com.mfpe.exception.OrderAlreadyPaidException;
import com.mfpe.exception.OrderDomainException;
import com.mfpe.exception.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(OrderNotFoundException ex){
        log.warn("Order not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler({OrderAlreadyPaidException.class, OrderAlreadyCancelledException.class} )
    public ResponseEntity<Map<String, Object>> handleConflict(OrderDomainException ex){
        log.warn("Order conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(OrderDomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(OrderDomainException ex){
        log.warn("Domain exception: {}", ex.getMessage());
        return ResponseEntity.badRequest().body(errorBody(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex){
        log.error("Unexpected error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody(
                "Interal server error: " + ex.getMessage()));
    }

    private Map<String, Object> errorBody(String message){
        return Map.of(
                "error", message,
                "timestamp", LocalDateTime.now().toString()
        );
    }


}
