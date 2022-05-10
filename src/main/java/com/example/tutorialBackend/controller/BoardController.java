package com.example.tutorialBackend.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.tutorialBackend.common.ErrorType;
import com.example.tutorialBackend.common.annotation.ExistCheck;
import com.example.tutorialBackend.dto.BoardListView;
import com.example.tutorialBackend.dto.BoardView;
import com.example.tutorialBackend.dto.request.BoardWriteInfo;
import com.example.tutorialBackend.service.BoardService;
import com.example.tutorialBackend.service.KafkaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/api/board")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BoardController {

    private final BoardService boardService;
    private final KafkaService kafkaService;

    @GetMapping("/list")
    public ResponseEntity<BoardListView> getBoardList(
        @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize
    ) {
        return ResponseEntity.ok(boardService.getBoardList(pageNo, pageSize));
    }

    @ExistCheck
    @GetMapping("/{id}")
    public ResponseEntity<BoardView> readBoard(@PathVariable Integer id) {
        log.info("[보드 읽기] : id : {}", id);
        return ResponseEntity.ok(boardService.readBoard(id));
    }

    @PostMapping
    public ResponseEntity<Map> writeBoard(HttpServletRequest httpServletRequest,
        @Validated @RequestBody BoardWriteInfo boardWriteInfo) {
        log.info("[보드 쓰기] : body : {}", boardWriteInfo.toString());
        boardService.writeBoard(boardWriteInfo, httpServletRequest.getRemoteAddr());
        kafkaService.sendMessage(String.format("[새 글 작성됨] %s", boardWriteInfo.getTitle()));
        return ResponseEntity.ok(ErrorType.SUCCESS.getInfo());
    }

    @ExistCheck
    @PutMapping("/{id}")
    public ResponseEntity<BoardView> updateBoard(HttpServletRequest httpServletRequest,
        @Validated @RequestBody BoardWriteInfo boardWriteInfo,
        @PathVariable Integer id
    ) {
        log.info("[보드 업데이트] : id : {} , body : {}", id, boardWriteInfo.toString());
        return ResponseEntity.ok(boardService.updateBoard(boardWriteInfo, id, httpServletRequest.getRemoteAddr()));
    }

    @ExistCheck
    @DeleteMapping("/{id}")
    public ResponseEntity<Map> deleteBoard(HttpServletRequest httpServletRequest,
        @PathVariable Integer id) {
        log.info("[보드 삭제] : No {}", id);
        boardService.deleteBoard(id, httpServletRequest.getRemoteAddr());
        return ResponseEntity.ok(ErrorType.SUCCESS.getInfo());
    }

    @GetMapping("/search")
    public ResponseEntity<BoardListView> searchBoard(
        @RequestParam(value = "title", required = true) String title,
        @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
        @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize)
    {
        log.info("[검색 정보] : title :{}, pageNo : {}, pageSize : {}", title, pageNo, pageSize);
        return ResponseEntity.ok(boardService.searchBoard(title, pageNo, pageSize));
    }
}
