package com.directa24.challenge.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@Builder
@ToString
@JsonIgnoreProperties(value = { "cause", "stackTrace", "suppressed", "localizedMessage" })
public class MovieException extends Exception {

    private HttpStatus status;
    private String message;

    public MovieException() {
    }

    public MovieException(HttpStatus status) {
        this();
        this.status = status;
    }

    public MovieException(HttpStatus status, Throwable ex) {
        this.status = status;
        this.message = ex.getLocalizedMessage();
    }

    public MovieException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public static MovieException parseException( Exception e ) {
        MovieException error;
        if( e instanceof  MovieException ) {
            error = (MovieException) e;
        } else {
            error = new MovieException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        log.error("Error: {}", error.getMessage());
        return error;
    }

}
