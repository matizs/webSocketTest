package com.example.tutorialBackend.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardListView {
    @JsonProperty
    private List<BoardView> results;

    @JsonProperty
    private Integer totalPages;
}
