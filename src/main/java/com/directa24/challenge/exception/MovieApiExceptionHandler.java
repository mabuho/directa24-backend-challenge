package com.directa24.challenge.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static com.directa24.challenge.utils.Contants.RESOURCE_NOT_FOUND;
import static com.directa24.challenge.utils.Contants.WRONG_REQUEST_PARAMETER;

@ControllerAdvice
public class MovieApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {


        return buildResponseEntity( MovieException.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(RESOURCE_NOT_FOUND)
                .build() );
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        return buildResponseEntity(MovieException.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(WRONG_REQUEST_PARAMETER)
                .build() );
    }

    private ResponseEntity<Object> buildResponseEntity(MovieException apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
