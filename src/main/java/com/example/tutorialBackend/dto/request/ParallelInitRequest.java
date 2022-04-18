package com.example.tutorialBackend.dto.request;

import lombok.Getter;

@Getter
public class ParallelInitRequest {
    String fileName;
    Integer partSize;
    Long totalSize;
}
