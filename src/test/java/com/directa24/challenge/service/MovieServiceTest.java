package com.directa24.challenge.service;

import com.directa24.challenge.config.MovieProperties;
import com.directa24.challenge.exception.MovieException;
import com.directa24.challenge.model.DirectorName;
import com.directa24.challenge.repository.MovieApiComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.directa24.challenge.utils.Contants.INIT_SEARCH_PAGE;
import static com.directa24.challenge.utils.Mocks.mockMovieException;
import static com.directa24.challenge.utils.Mocks.mockMovieApiResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MovieServiceTest {

    @Mock
    private MovieApiComponent movieApi;

    @Mock
    private MovieProperties movieProps;

    @InjectMocks
    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        when(movieProps.getProperties(eq("url.page"), eq(INIT_SEARCH_PAGE)))
                .thenReturn(INIT_SEARCH_PAGE);
    }

    @Test
    public void test_givenThreshold_getNonEmptyListOfDirectorNames() throws Exception {

        doNothing().when( movieApi ).fetchMoviesFromApi( anyInt() );
        doReturn( mockMovieApiResponse() ).when( movieApi ).getMovies();

        Optional<DirectorName> directorNames =
                movieService.getDirectorNamesFilteredByThreshold( 1 );

        assertTrue(directorNames.isPresent(), "The optional object is null.");
        Set<String> names = directorNames.get().getNames();
        assertFalse(names.isEmpty(), "The list of names is empty");
        assertEquals(2, names.size(), "The size of the response does not match");

        verify(movieApi, atLeastOnce()).fetchMoviesFromApi(anyInt());

    }

    @Test
    public void test_givenThreshold_getEmptyListOfDirectorNames() throws Exception {

        doReturn( mockMovieApiResponse() ).when( movieApi ).getMovies();

        Optional<DirectorName> directorNames =
                movieService.getDirectorNamesFilteredByThreshold( 2 );

        assertFalse(directorNames.isPresent(), "The optional object is not null.");

        verify(movieApi, atLeastOnce()).fetchMoviesFromApi(anyInt());

    }

    @Test
    public void test_givenThreshold_whenFetchingMoviesFromApi_thenMovieExceptionIsThrown() throws Exception {

        doThrow( mockMovieException("Mock some exception message") ).when( movieApi ).fetchMoviesFromApi( anyInt() );

        Exception ex = assertThrows(MovieException.class, () -> {
            movieService.getDirectorNamesFilteredByThreshold( 1 );
        });

        String expectedMessage = "exception message";

        assertTrue(ex instanceof MovieException);

        String actualMessage = ((MovieException) ex).getMessage();
        HttpStatus actualStatus = ((MovieException) ex).getStatus();

        assertTrue( actualMessage.contains(expectedMessage) );
        assertEquals( HttpStatus.INTERNAL_SERVER_ERROR, actualStatus);
        verify(movieApi, atLeastOnce()).fetchMoviesFromApi(anyInt());

    }

}
