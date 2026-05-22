package com.yasirkhan.user.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.yasirkhan.user.responses.ErrorResponse;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistException(UserAlreadyExistException ex, HttpServletRequest request){

        log.warn("Registration rejected: {}", ex.getMessage());

        ErrorResponse error = ErrorResponse.builder()
                .message(ex.getMessage())
                .status(ex.getStatus().value())
                .error(ex.getStatus().getReasonPhrase())
                .timeStamp(LocalDateTime.now())
                .path(request.getRequestURI())
                //.traceId(getTraceId())
                .build();

        return new ResponseEntity<>(error, ex.getStatus());
    }

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

    @ExceptionHandler({UnauthorizedException.class, io.jsonwebtoken.JwtException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationExceptions(Exception exception, HttpServletRequest request) {

        // If it's a raw JwtException (like signature tampered), customize the message
        String message = (exception instanceof io.jsonwebtoken.JwtException)
                ? "Invalid or Expired JWT Token"
                : exception.getMessage();

        ErrorResponse response =
                ErrorResponse
                        .builder()
                        .message(message)
                        .status(HttpStatus.UNAUTHORIZED.value()) // 401
                        .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                        .timeStamp(LocalDateTime.now())
                        .path(request.getRequestURI())
                        .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGlobalRuntimeException(RuntimeException ex, HttpServletRequest request) {

        log.error("Unexpected Runtime Exception caught at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        ErrorResponse response =
                ErrorResponse
                        .builder()
                        .message("An unexpected internal server error occurred.")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                        .path(request.getRequestURI())
                        .timeStamp(LocalDateTime.now())
                        .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

