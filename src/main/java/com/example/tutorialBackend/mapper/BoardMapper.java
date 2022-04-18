package com.example.tutorialBackend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.tutorialBackend.domain.Board;
import com.example.tutorialBackend.dto.BoardView;

@Mapper(componentModel = "spring")
public interface BoardMapper {

    @Mapping(target = "body", source = "body.body")
    @Mapping(target = "files", source = "files")
    BoardView toBoardDTO(Board board);
}
