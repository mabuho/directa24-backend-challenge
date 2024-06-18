package com.directa24.challenge.service;

import com.directa24.challenge.model.DirectorName;
import com.directa24.challenge.model.Movie;

import java.util.List;
import java.util.Optional;

public interface IMovieService {

    List<Movie> getAllMovies() throws Exception;

    Optional<DirectorName> getDirectorNamesFilteredByThreshold(final int threshold) throws Exception;

}
