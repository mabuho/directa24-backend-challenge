package com.directa24.challenge.utils;

import com.directa24.challenge.exception.MovieException;
import com.directa24.challenge.model.DirectorName;
import com.directa24.challenge.model.Movie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.util.*;

public class Mocks {

    public static Optional<DirectorName> mockDirectorName() {
        return Optional.of( DirectorName.builder()
                .names( Set.of("Director1", "Director2") )
                .build() );
    }

    public static List<Movie> mockMovieApiResponse() {
        Movie movie1 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director1")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        Movie movie2 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director1")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        Movie movie3 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        Movie movie4 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director3")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        Movie movie5 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director4")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        Movie movie6 = Movie.builder()
                .year("year")
                .title("title")
                .rated("rate")
                .released("release date")
                .runtime("runtime")
                .genre("genre1, genre2, genre3")
                .director("director")
                .writer("writer")
                .actors("actor1, actor2, actor3")
                .build();
        List<Movie> movies = new ArrayList<>();
        movies.add(movie1);
        movies.add(movie2);
        movies.add(movie3);
        movies.add(movie4);
        movies.add(movie5);
        movies.add(movie6);
        return movies;
    }

    public static MovieException mockMovieException(String message) {
        return MovieException.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(message)
                .build();
    }

    public static HttpClientErrorException mockHttpClientErrorException(
            String message,
            String statusText,
            int statusCode
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept", "application/json");
        Charset charset = Charset.defaultCharset();
        HttpClientErrorException tooManyRequests = HttpClientErrorException.create(
                message,
                HttpStatus.valueOf(statusCode),
                statusText,
                headers,
                null,
                charset
        );
        return tooManyRequests;
    }

    public static ResponseEntity<MovieResponsePage<Movie>> mockResponseEntity(int page, int totalPages) {
        MovieResponsePage<Movie> responsePage =
                new MovieResponsePage<>(
                        page,
                        6,
                        1L,
                        totalPages,
                        mockMovieApiResponse());
        return ResponseEntity.of( Optional.of(responsePage) );
    }

    public static ResponseEntity<MovieResponsePage<Movie>> mockResponseEntity(int totalPages) {
        return  mockResponseEntity(1, totalPages);
    }

}
