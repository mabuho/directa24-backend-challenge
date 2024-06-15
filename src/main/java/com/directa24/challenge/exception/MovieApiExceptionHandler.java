package com.directa24.challenge.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class MovieApiExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String error = "The resource you're trying to reach does not exist.";
        return buildResponseEntity(new MovieException(HttpStatus.NOT_FOUND, error));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String path = ((ServletWebRequest) request).toString();
        path = path.substring(path.indexOf("uri") + "uri=".length(), path.indexOf(";")-1);
        String error = "";
        if ( !path.contains("?threshold=")) {
            error = "The path does not contains the proper request parameter.";
        }
        return buildResponseEntity(new MovieException(HttpStatus.BAD_REQUEST, error));
    }

    private ResponseEntity<Object> buildResponseEntity(MovieException apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

}
