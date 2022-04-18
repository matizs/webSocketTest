package com.example.tutorialBackend.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BoardView {
    @JsonProperty
    private Integer id;

    @JsonProperty
    private String title;

    @JsonProperty
    private String body;

    @JsonProperty
    private List<BoardFileView> files = new ArrayList<>();

    @JsonProperty
    private LocalDateTime createdAt;

    @JsonProperty
    private LocalDateTime updatedAt;

    @JsonProperty
    private String createdBy;

    @JsonProperty
    private String updatedBy;
}
