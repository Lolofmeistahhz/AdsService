package com.example.userservice.controller;

import com.example.userservice.controller.dto.ErrorDto;
import com.example.userservice.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class UserControllerAdvice {

    private static final String USER_PROCESSING_EXCEPTION_MESSAGE = "Exception during user processing! {}";

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorDto> handleUserException(UserException e) {
        log.error(USER_PROCESSING_EXCEPTION_MESSAGE, e.getMessage());
        return buildApiException(HttpStatus.NOT_FOUND, e.getMessage(), "User error", "user-error-code");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnknownException(Exception e) {
        log.error(USER_PROCESSING_EXCEPTION_MESSAGE, e.getMessage());
        return buildApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage(), "General error", "general-error-code");
    }

    private ResponseEntity<ErrorDto> buildApiException(HttpStatus httpStatus, String message, String title, String errorCode) {
        return ResponseEntity.status(httpStatus)
                .body(ErrorDto.builder()
                        .title(title)
                        .detail(message)
                        .code(errorCode)
                        .build());
    }
}