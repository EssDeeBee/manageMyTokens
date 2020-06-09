package com.ser.webutils.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import com.ser.rest.response.Response;

import java.util.Optional;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity nullPointerException(WebExchangeBindException ex) {

        BindingResult result = ex.getBindingResult();

        String message = "Unknown error";
        Optional<FieldError> firstError = result.getFieldErrors().stream().findFirst();
        if (firstError.isPresent()) {
            message = firstError.get().getDefaultMessage();
        }

        log.error("Response sent: " + message);
        return ResponseEntity.status(BAD_REQUEST).body(new Response(400, message));
    }
}
