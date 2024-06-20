package com.directa24.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class DirectorName {

    @JsonProperty("directors")
    private Set<String> names;

}
