package com.directa24.challenge.controller;

import static com.directa24.challenge.utils.Contants.DIRECTORS_NOT_FOUND;

import com.directa24.challenge.exception.MovieException;
import com.directa24.challenge.model.DirectorName;
import com.directa24.challenge.service.MovieService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/directors")
public class MovieController implements IMovieController {

    private final MovieService service;

    @Override
    public ResponseEntity<?> getDirectorsByThreshold(int threshold) {
        log.debug("Threshold: {}", threshold);

        Optional<DirectorName> names;
        try {
            names = service.getDirectorNamesFilteredByThreshold(threshold);
        } catch (Exception e) {
            MovieException error = MovieException.parseException( e );
            return ResponseEntity.status(error.getStatus()).body(error);
        }

        if( !names.isPresent() ) {
            log.info(String.format(DIRECTORS_NOT_FOUND, threshold));
            MovieException error = new MovieException(
                    HttpStatus.NOT_FOUND,
                    String.format(DIRECTORS_NOT_FOUND, threshold));
            return ResponseEntity.status(error.getStatus()).body(error);
        }

        log.info("There are [ {} ] names found.", names.get().getNames().size());
        names.get().getNames().forEach(log::info);
        return ResponseEntity.ok(names.get());
    }

}

