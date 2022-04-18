package com.example.tutorialBackend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardFileView {
    private Integer id;
    private String originalFileName;
    private String nubesFileName;
    private Long size;
    private LocalDateTime createdAt;
    private String createdBy;
}
