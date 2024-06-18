package com.directa24.challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

public interface IMovieController {

    @GetMapping
    ResponseEntity<?> getMovieDirectorsByThreshold(@RequestParam(value = "threshold") int threshold) throws Exception;

}
