package com.directa24.challenge.service;

import static com.directa24.challenge.utils.Contants.INIT_SEARCH_PAGE;

import com.directa24.challenge.config.MovieProperties;
import com.directa24.challenge.model.DirectorName;
import com.directa24.challenge.model.Movie;
import com.directa24.challenge.repository.MovieApiComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MovieService implements IMovieService {

    private final MovieProperties movieProps;
    private final MovieApiComponent movieApi;

    @Override
    public Optional<DirectorName> getDirectorNamesFilteredByThreshold(final int threshold) throws Exception {
        log.debug("Inside getDirectorNamesFilteredByThreshold method...");
        List<Movie> movies = getAllMovies();

        List<String> names = movies.stream()
                .collect(Collectors.groupingBy(o -> o.getDirector()))
                .entrySet().stream()
                    .filter(m -> m.getValue().size() > threshold)
                    .map(m -> m.getKey())
                    .sorted()
                    .collect(Collectors.toList());

        return names.isEmpty()
                    ? Optional.empty()
                    : Optional.of( DirectorName.builder().names( names ).build() );
    }

    @Override
    public List<Movie> getAllMovies() throws Exception {
        log.debug("Inside getAllMovies method...");
        String page = movieProps.getProperties("url.page", INIT_SEARCH_PAGE);
        movieApi.fetchMoviesFromApi( Integer.valueOf(page) );

        return movieApi.getMovies().isEmpty()
                ? Collections.emptyList()
                : movieApi.getMovies();
    }

}
