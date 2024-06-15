package com.directa24.challenge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties("directa24")
public class MovieProperties {

    private final Map<String, String> movie = new HashMap<>();

    public String getProperties(Object prop, String defaultValue) {
        if(movie.containsKey(prop))
            return movie.get(prop);
        return defaultValue;
    }


}
