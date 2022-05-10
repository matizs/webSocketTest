package com.example.tutorialBackend.controller;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;

import com.example.tutorialBackend.dto.chat.ChatMessage;
import com.example.tutorialBackend.dto.response.SendChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

@EnableWebSocketMessageBroker
@AllArgsConstructor
@Controller
public class ChattingController {
    private final SimpMessagingTemplate template;
    private final ObjectMapper mapper;
    @MessageMapping("/chat")
    public void chatSend(ChatMessage message) {
        SendChatMessage scm = new SendChatMessage();
        scm.setOpcode("Chatting");
        try {
            scm.setPayload(mapper.writeValueAsString(message));
            template.convertAndSend("/subscribe", scm);
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(message);
    }
}
