package com.example.tutorialBackend.handler;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlarmHandler extends TextWebSocketHandler {
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        String payload = message.getPayload();
        log.info("payload : {}", payload);

    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer payload = message.getPayload();
        log.info("payload2 : {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }
}
