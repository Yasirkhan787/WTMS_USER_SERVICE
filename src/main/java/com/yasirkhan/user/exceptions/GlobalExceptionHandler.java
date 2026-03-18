package com.yasirkhan.user.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yasirkhan.user.responses.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotException(ResourceNotFoundException exception, HttpServletRequest request) {

        ErrorResponse response =
                ErrorResponse
                        .builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .timeStamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<ErrorResponse> handleDatabaseException(DatabaseException exception, HttpServletRequest request) {

        ErrorResponse response =
                ErrorResponse
                        .builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .timeStamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException exception, HttpServletRequest request) {

        ErrorResponse response =
                ErrorResponse
                        .builder()
                        .message(exception.getMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .timeStamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .build();
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, HttpServletRequest request) {

        // Default to 500
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

        // Dynamically switch status based on the exception type
        if (ex instanceof org.springframework.web.bind.MethodArgumentNotValidException) {
            status = HttpStatus.BAD_REQUEST; // Validation failed
        } else if (ex instanceof org.springframework.dao.DataIntegrityViolationException) {
            status = HttpStatus.CONFLICT;    // Database constraint (like your 'status' check)
        } else if (ex instanceof org.springframework.web.HttpRequestMethodNotSupportedException) {
            status = HttpStatus.METHOD_NOT_ALLOWED;
        }

        ErrorResponse response = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(status.value())
                .error(status.getReasonPhrase())
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(response, status);
    }
}
