package com.directa24.challenge.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Configuration
public class MovieApiConfig {

    @Bean
    public RestTemplate movieRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter messageConverter =
                new MappingJackson2HttpMessageConverter();
        messageConverter.setSupportedMediaTypes(
                Arrays.asList(
                        MediaType.APPLICATION_JSON,
                        MediaType.APPLICATION_OCTET_STREAM));
        restTemplate.getMessageConverters().add(messageConverter);
        return restTemplate;
    }

}
