package com.example.tutorialBackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.tutorialBackend.common.ErrorType;
import com.example.tutorialBackend.domain.Board;
import com.example.tutorialBackend.domain.BoardBody;
import com.example.tutorialBackend.domain.BoardFile;
import com.example.tutorialBackend.dto.BoardFileView;
import com.example.tutorialBackend.dto.BoardListView;
import com.example.tutorialBackend.dto.BoardView;
import com.example.tutorialBackend.dto.request.BoardWriteInfo;
import com.example.tutorialBackend.exception.NotFoundException;
import com.example.tutorialBackend.mapper.BoardMapper;
import com.example.tutorialBackend.repository.BoardBodyRepository;
import com.example.tutorialBackend.repository.BoardFileRepository;
import com.example.tutorialBackend.repository.BoardRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardBodyRepository boardBodyRepository;
    private final BoardFileRepository boardFileRepository;
    private final BoardMapper boardMapper;

    public BoardService(BoardRepository boardRepository, BoardBodyRepository boardBodyRepository,
        BoardFileRepository boardFileRepository, BoardMapper boardMapper) {
        this.boardRepository = boardRepository;
        this.boardBodyRepository = boardBodyRepository;
        this.boardFileRepository = boardFileRepository;
        this.boardMapper = boardMapper;
    }

    @Transactional
    public BoardView readBoard(Integer id) {
        Board board = boardRepository.findById(id).orElseThrow(new NotFoundException(ErrorType.NOT_EXIST_BOARD));
        return boardMapper.toBoardDTO(board);
    }

    @Transactional
    public void writeBoard(BoardWriteInfo boardWriteInfo, String createdBy) {
        Board board = createBoardAndSave(boardWriteInfo.getTitle(), createdBy);
        BoardBody boardBody = createBoardBodyAndSave(boardWriteInfo.getBody(), board);

        findFileAndSave(boardWriteInfo.getFiles(), board);
        board.setBody(boardBody);
    }

    @Transactional
    public BoardView updateBoard(BoardWriteInfo boardWriteInfo, Integer id, String updatedBy) {
        Board board = boardRepository.findById(id).orElseThrow(new NotFoundException(ErrorType.NOT_EXIST_BOARD));
        board.setTitle(boardWriteInfo.getTitle());
        board.getBody().setBody(boardWriteInfo.getBody());
        board.setUpdatedAt(LocalDateTime.now());
        board.setUpdatedBy(updatedBy);

        List<BoardFileView> newFiles = boardWriteInfo.getFiles().stream().filter(boardFileView ->
            board.getFiles().stream().allMatch(file -> boardFileView.getId() != file.getId())
        ).collect(Collectors.toList());

        findFileAndSave(newFiles, board);

        return boardMapper.toBoardDTO(board);
    }

    @Transactional
    public void deleteBoard(Integer id, String updatedBy) {
        Board board = boardRepository.findById(id).orElseThrow(new NotFoundException(ErrorType.NOT_EXIST_BOARD));
        board.setUpdatedBy(updatedBy);
        boardRepository.delete(board);
    }

    @Transactional
    public BoardListView getBoardList(Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Board> pages = boardRepository.findAll(pageRequest);

        return BoardListView.builder()
            .results(toBoardViewList(pages.getContent()))
            .totalPages(pages.getTotalPages())
            .build();
    }

    @Transactional
    public BoardListView searchBoard(String title, Integer pageNo, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        Page<Board> pages = boardRepository.findByTitleContainingIgnoreCase(pageRequest, title);

        return BoardListView.builder()
            .results(toBoardViewList(pages.getContent()))
            .totalPages(pages.getTotalPages())
            .build();
    }

    private List<BoardView> toBoardViewList(List<Board> boardList) {
        return boardList.stream().map(boardMapper::toBoardDTO).collect(Collectors.toList());
    }

    private Board createBoardAndSave(String title, String createdBy) {
        Board board = Board.builder()
            .title(title)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .createdBy(createdBy)
            .updatedBy(createdBy)
            .files(new ArrayList<>())
            .build();
        boardRepository.save(board);
        return board;
    }

    private BoardBody createBoardBodyAndSave(String body, Board board) {
        BoardBody boardBody = BoardBody.builder()
            .body(body)
            .board(board)
            .build();
        boardBodyRepository.save(boardBody);
        return boardBody;
    }

    private void findFileAndSave(List<BoardFileView> files, Board board) {
        for (BoardFileView newFile : files) {
            BoardFile boardFile = boardFileRepository.findById(newFile.getId()).get();
            boardFile.setBoard(board);
            boardFileRepository.save(boardFile);
            board.getFiles().add(boardFile);
        }
    }
}
