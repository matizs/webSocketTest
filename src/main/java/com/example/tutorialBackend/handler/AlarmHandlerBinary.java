package com.example.tutorialBackend.handler;

import java.nio.ByteBuffer;

import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AlarmHandlerBinary extends BinaryWebSocketHandler {

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        ByteBuffer payload = message.getPayload();
        log.info("payload2 : {}", payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }
}
