package com.directa24.challenge.repository;

import static com.directa24.challenge.utils.Contants.MOVIES_URL;

import com.directa24.challenge.config.MovieProperties;
import com.directa24.challenge.exception.MovieException;
import com.directa24.challenge.model.Movie;
import com.directa24.challenge.utils.MovieResponsePage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Component
@RequiredArgsConstructor
public class MovieApiComponent {

    private final RetryTemplate retryTemplate;
    private final MovieProperties movieProps;
    private final RestTemplate movieRestTemplate;

    private List<Movie> movies;

    public void fetchMoviesFromApi(int page) throws Exception {
        log.debug("Current page: {}", page);
        movies = new ArrayList<>();
        String url = movieProps.getProperties("url", MOVIES_URL);
        UriComponentsBuilder uriBuilder =
                UriComponentsBuilder.fromUriString(url);
        uriBuilder.queryParam("page", page);
        log.debug("Calling Movies API: {}", uriBuilder.toUriString());

        Map<String, ResponseEntity> responseMap = new HashMap<>();
        // I used a retryTemplate because I was getting the following exception:
        // org.springframework.web.client.HttpClientErrorException$TooManyRequests:: 429 Too Many Requests: "Rate limit exceeded"
        // and
        // org.springframework.web.client.HttpClientErrorException$TooManyRequests:: 404 Not Found: "{<EOL>  "errors" : [ "Monthly request quota has been exceeded. Visit https://app.wiremock.cloud/account/subscriptions to upgrade." ]<EOL>}"
        retryTemplate.execute( ctx -> {
            log.debug("Inside retryTemplate.... ");
            ResponseEntity<MovieResponsePage<Movie>> moviePage;
            try {
                moviePage = movieRestTemplate.exchange(
                        uriBuilder.toUriString(),
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<>() {
                        }
                );
            } catch (HttpClientErrorException.NotFound e) {
                log.error("Exception: {}", e.getMessage());
                throw new MovieException(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
            }

            responseMap.put("moviePage", moviePage);
            return responseMap;
        });

        ResponseEntity<MovieResponsePage<Movie>> moviePage = responseMap.get("moviePage");
        if( moviePage == null) return;

        if ( !moviePage.getBody().getContent().isEmpty() ) {
            int size = moviePage.getBody().getContent().size();
            log.debug("Adding {} new elements to the list", size);
            List<Movie> ms = moviePage.getBody().getContent();
            movies.addAll( ms );
        }

        if( !moviePage.getBody().hasNext() ) {
            log.debug("There are no more pages to fetch data...");
            return;
        }

        log.debug("There are more pages to fetch data...");
        page = moviePage.getBody().nextPageable().getPageNumber();
        log.debug("Next page: {} ", page);
        fetchMoviesFromApi(page);
    }

}


