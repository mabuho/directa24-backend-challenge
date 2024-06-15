package com.directa24.challenge.utils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;

public class MovieResponsePage<T> extends PageImpl<T> {

    @JsonCreator
    public MovieResponsePage(
            @JsonProperty("page") int page,
            @JsonProperty("per_page") int perPage,
            @JsonProperty("total") Long total,
            @JsonProperty("total_pages") int totalPages,
            @JsonProperty("data") List<T> data
    ) {
        super(data, PageRequest.of(page, 1), totalPages + 1);
    }

    public MovieResponsePage(List<T> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    public MovieResponsePage(List<T> content) {
        super(content);
    }

    public MovieResponsePage() {
        super(new ArrayList<>());
    }

}
