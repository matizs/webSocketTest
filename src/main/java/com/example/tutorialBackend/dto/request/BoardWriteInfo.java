package com.example.tutorialBackend.dto.request;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.example.tutorialBackend.dto.BoardFileView;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardWriteInfo {
    @NotBlank
    private String title;

    @NotNull
    private String body;

    private List<BoardFileView> files;
}
