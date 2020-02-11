package com.silaev.kalah.controller;

import com.silaev.kalah.model.ErrorMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author Konstantin Silaev on 2/9/2020
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorMessage> handleException(Exception e) {
        log.debug(
            "GlobalExceptionHandler: handleException. message: {}, cause: {}",
            e.getMessage(), e.getCause()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorMessage.builder().message(e.getMessage()).build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleGeneralException(Exception e) {
        log.debug(
            "GlobalExceptionHandler: handleGeneralException. message: {}, cause: {}",
            e.getMessage(), e.getCause()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(
                ErrorMessage.builder()
                    .message("Internal error occurred, please contact an administrator")
                    .build()
            );
    }
}
