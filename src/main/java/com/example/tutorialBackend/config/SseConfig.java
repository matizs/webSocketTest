package com.example.tutorialBackend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.tutorialBackend.service.SseService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SseConfig {
    private final SseService sseService;
    private final int timeout = 600; //second

    @GetMapping("/sseConnect")
    @CrossOrigin("*")
    public SseEmitter connect(@RequestParam String id) {
        SseEmitter emitter = new SseEmitter(timeout * 1000L);
        sseService.addUser(id, emitter);
        return emitter;
    }

}
