package com.odeyalo.sonata.playlists.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandlerController {

    @ExceptionHandler(InvalidPaginationLimitException.class)
    public ResponseEntity<Void> handleInvalidPaginationLimitException(InvalidPaginationLimitException ex) {
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(InvalidPaginationOffsetException.class)
    public ResponseEntity<Void> handleInvalidPaginationOffsetException(InvalidPaginationOffsetException ex) {
        return ResponseEntity.badRequest().build();
    }
}
