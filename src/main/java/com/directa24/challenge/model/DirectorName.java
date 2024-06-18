package com.directa24.challenge.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DirectorName {

    @JsonProperty("directors")
    private List<String> names;

}
