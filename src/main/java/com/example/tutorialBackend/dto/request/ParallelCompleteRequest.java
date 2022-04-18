package com.example.tutorialBackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParallelCompleteRequest {

    String fileName;
    String uploadKey;
    Long fileSize;
}
