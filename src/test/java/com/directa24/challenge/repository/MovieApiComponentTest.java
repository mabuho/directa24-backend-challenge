package com.directa24.challenge.repository;

import com.directa24.challenge.config.MovieProperties;
import com.directa24.challenge.config.RetryConfig;
import com.directa24.challenge.exception.MovieException;
import com.directa24.challenge.model.Movie;
import com.directa24.challenge.utils.MovieResponsePage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.Extensions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;

import static com.directa24.challenge.utils.Contants.INIT_SEARCH_PAGE;
import static com.directa24.challenge.utils.Mocks.mockHttpClientErrorException;
import static com.directa24.challenge.utils.Mocks.mockResponseEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@EnableConfigurationProperties({RetryConfig.class, MovieProperties.class})
@PropertySource("classpath:application.properties")
@EnableRetry
@Extensions({
        @ExtendWith(SpringExtension.class),
        @ExtendWith(MockitoExtension.class)
})
public class MovieApiComponentTest {

    @Autowired
    private RetryTemplate retryTemplate;

    @Mock
    private RestTemplate movieRestTemplate;

    @Autowired
    private MovieProperties movieProps;

    @InjectMocks
    private MovieApiComponent movieApi;

    private ParameterizedTypeReference<MovieResponsePage<Movie>> parameterizedTypeReference;

    @BeforeEach
    public void setUp() {
        movieApi= new MovieApiComponent(retryTemplate, movieProps, movieRestTemplate);
        parameterizedTypeReference = new ParameterizedTypeReference<>(){};
    }

    @Test
    public void givenThePageNumber_fetchMoviesFromTheApi_ThenPopulateTheList() throws Exception {
        // Simulate the ResponseEntity has 'n' pages,
        // for this test case, there are two pages, each Page/ResponseEntity has 6 elements
        when(movieRestTemplate.exchange(
                // In order to make the movieRestTemplate to call 'n' times the movies url
                // a regex matcher is passed; it matches the URL plus the value
                // of the page queryParameter
                // If we don't do this, and we use the ArgumentMatcher 'eq' instead
                //      i.e.:   eq(uriBuilder)
                // the movieRestTemplate will be executed only once.
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference)))
                .thenReturn( mockResponseEntity(2) )
                .thenReturn( mockResponseEntity(2, 2) );

        movieApi.fetchMoviesFromApi( Integer.valueOf(INIT_SEARCH_PAGE) );

        List<Movie> movies = movieApi.getMovies();

        assertFalse(movies.isEmpty(), "The list is empty");
        assertEquals(12, movies.size(), "The size of the list does not match");

        verify(movieRestTemplate, times(2))
            .exchange(
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference));
    }

    @Test
    @Description("When a ResponseEntity has more pages calls the method recursively "
            + "If the service throws a TooManyRequests exception, the RetryTemplate will try "
            + "to execute the method on 3 attempts with every 150 milliseconds"
    )
    public void whenMultiplePagesCallMethodRecursively_thenThrowsTooManyRequestsException_thenRetry_thenPopulateList() throws Exception {
        when(movieRestTemplate.exchange(
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference))
        )
        .thenReturn(mockResponseEntity(3))
        .thenReturn(mockResponseEntity(2, 3))
        // For the  last call simulates it throws an exception,
        // it retries two times and the third succeeds
        // Fails on 1st attempt and 2nd attempt
        .thenThrow( mockHttpClientErrorException("Rate limit exceeded", "429 Too Many Requests ", 429) )
        .thenThrow( mockHttpClientErrorException("Rate limit exceeded", "429 Too Many Requests ", 429) )
        // Succeed on 3rd attempt
        .thenReturn(mockResponseEntity(3, 3));

        movieApi.fetchMoviesFromApi( Integer.valueOf(INIT_SEARCH_PAGE) );

        List<Movie> movies = movieApi.getMovies();

        assertFalse(movies.isEmpty(), "The list is empty");
        assertEquals(18, movies.size(), "The size of the list does not match");

        verify(movieRestTemplate, times(5))
            .exchange(
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference));
    }

    @Test
    public void whenMoviesApiIsNotAvailable_thenThrowsMovieException() {
        when(movieRestTemplate.exchange(
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference)))
        .thenThrow( mockHttpClientErrorException("Service not available", "404 Not Found", 404));


        Exception ex = assertThrows(MovieException.class, () -> {
            movieApi.fetchMoviesFromApi( Integer.valueOf(INIT_SEARCH_PAGE) );
        });

        String expectedMessage = "Service not available";
        String actualMessage = ex.getMessage();
        HttpStatus actualStatus = ((MovieException) ex).getStatus();

        assertTrue( actualMessage.equalsIgnoreCase(expectedMessage) );
        assertEquals( HttpStatus.SERVICE_UNAVAILABLE, actualStatus);

        verify(movieRestTemplate, atLeastOnce())
            .exchange(
                matches("(.+\\?page=)[0-9]+"),
                eq(HttpMethod.GET),
                eq(null),
                eq(parameterizedTypeReference));

    }

}
