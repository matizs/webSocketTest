package com.example.tutorialBackend.mapper;

import org.mapstruct.Mapper;

import com.example.tutorialBackend.domain.BoardFile;
import com.example.tutorialBackend.dto.BoardFileView;

@Mapper(componentModel = "spring")
public interface BoardFileMapper {

    BoardFileView toBoardFileView(BoardFile file);
}
