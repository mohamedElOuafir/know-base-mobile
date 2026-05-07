package com.servicebackend.handler;


import com.servicebackend.exceptions.FileNotUploadedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;
import java.util.Map;

@RestController
@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(FileNotUploadedException.class)
    public ResponseEntity<?> handleException(FileNotUploadedException e) {
        return ResponseEntity
                .badRequest()
                .body(
                        Map.of(
                                "message", e.getMessage(),
                                "uploaded", false
                        )
                );

    }
}
