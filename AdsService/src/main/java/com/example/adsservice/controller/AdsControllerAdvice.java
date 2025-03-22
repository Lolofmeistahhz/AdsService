package com.example.adsservice.controller;

import com.example.adsservice.controller.dto.ErrorDto;
import com.example.adsservice.exception.AdsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class AdsControllerAdvice {

    private static final String ADS_PROCESSING_EXCEPTION_MESSAGE = "Exception during ads processing! {}";

    @ExceptionHandler(AdsException.class)
    public ResponseEntity<ErrorDto> handleAdsException(AdsException e) {
        log.error(ADS_PROCESSING_EXCEPTION_MESSAGE, e.getMessage());
        return buildApiException(HttpStatus.NOT_FOUND, e.getMessage(), "Ads error", "ads-error-code");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnknownException(Exception e) {
        log.error(ADS_PROCESSING_EXCEPTION_MESSAGE, e.getMessage());
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