package com.directa24.challenge.controller;

import com.directa24.challenge.service.MovieService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static com.directa24.challenge.utils.Contants.*;
import static com.directa24.challenge.utils.Mocks.mockDirectorName;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest( MovieController.class )
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MovieService movieService;

    private static final String DIRECTORS_PATH = "/api/directors";
    private static final String QUERY_PARAM = "threshold";
    private static final int THRESHOLD = 4;

    @Test
    public void setUp() throws Exception {
    }

    @Test
    public void givenThreshold_thenReturnNonEmptyList() throws Exception {

        when( movieService.getDirectorNamesFilteredByThreshold( anyInt() ) )
                .thenReturn( mockDirectorName() );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(DIRECTORS_PATH)
                        .param(QUERY_PARAM, String.valueOf(THRESHOLD))
                        .accept(MediaType.APPLICATION_JSON)
            )
                .andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( jsonPath("$.status" ).doesNotExist() )
                .andExpect( jsonPath("$.message" ).doesNotExist() )
                .andExpect( jsonPath("$.cause" ).doesNotExist() )
                .andExpect( jsonPath("$.stackTrace" ).doesNotExist() )
                .andExpect( jsonPath("$.suppressed" ).doesNotExist() )
                .andExpect( jsonPath("$.localizedMessage" ).doesNotExist() )
                .andExpect( jsonPath("$.directors" ).exists() )
                .andExpect( jsonPath("$.directors[*]" ).isNotEmpty() );

    }

    @Test
    public void givenThreshold_thenReturnDirectorsNotFoundMessage() throws Exception {

        when( movieService.getDirectorNamesFilteredByThreshold( anyInt() ) )
                .thenReturn( Optional.empty() );

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(DIRECTORS_PATH)
                        .param(QUERY_PARAM, String.valueOf(THRESHOLD))
                        .accept(MediaType.APPLICATION_JSON)
            )
                .andDo( print() )
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath("$.directors" ).doesNotExist() )
                .andExpect( jsonPath("$.cause" ).doesNotExist() )
                .andExpect( jsonPath("$.stackTrace" ).doesNotExist() )
                .andExpect( jsonPath("$.suppressed" ).doesNotExist() )
                .andExpect( jsonPath("$.localizedMessage" ).doesNotExist() )
                .andExpect( jsonPath("$.status" ).exists() )
                .andExpect( jsonPath("$.status" ).value(HttpStatus.NOT_FOUND.name()) )
                .andExpect( jsonPath("$.message" ).exists() )
                .andExpect(jsonPath("$.message" )
                    .value(String.format(DIRECTORS_NOT_FOUND, THRESHOLD)));

    }

    @Test
    public void givenWrongPath_thenReturnResourceNotFoundMessage() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get("/wrongPath")
                        .accept(MediaType.APPLICATION_JSON)
            )
                .andDo( print() )
                .andExpect( status().isNotFound() )
                .andExpect( jsonPath("$.directors" ).doesNotExist() )
                .andExpect( jsonPath("$.cause" ).doesNotExist() )
                .andExpect( jsonPath("$.stackTrace" ).doesNotExist() )
                .andExpect( jsonPath("$.suppressed" ).doesNotExist() )
                .andExpect( jsonPath("$.localizedMessage" ).doesNotExist() )
                .andExpect( jsonPath("$.status").exists() )
                .andExpect( jsonPath("$.status" ).value(HttpStatus.NOT_FOUND.name()) )
                .andExpect( jsonPath("$.message" ).exists() )
                .andExpect( jsonPath("$.message" ).value(RESOURCE_NOT_FOUND));
    }

    @Test
    public void givenWrongQueryParam_thenReturnWrongRequestParameterMessage() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders
                        .get(DIRECTORS_PATH)
                        .param("wrongQueryParam", String.valueOf(THRESHOLD))
                        .accept(MediaType.APPLICATION_JSON)
            )
                .andDo( print() )
                .andExpect( status().isBadRequest())
                .andExpect( jsonPath("$.directors" ).doesNotExist() )
                .andExpect( jsonPath("$.cause" ).doesNotExist() )
                .andExpect( jsonPath("$.stackTrace" ).doesNotExist() )
                .andExpect( jsonPath("$.suppressed" ).doesNotExist() )
                .andExpect( jsonPath("$.localizedMessage" ).doesNotExist() )
                .andExpect( jsonPath("$.status" ).exists() )
                .andExpect( jsonPath("$.status" ).value(HttpStatus.BAD_REQUEST.name()) )
                .andExpect( jsonPath("$.message" ).exists() )
                .andExpect( jsonPath("$.message" ).value(WRONG_REQUEST_PARAMETER));
    }

}
