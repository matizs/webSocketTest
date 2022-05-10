package com.example.tutorialBackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.example.tutorialBackend.handler.AlarmHandlerBinary;

@Configuration
@EnableWebSocket
public class RawWebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AlarmHandlerBinary(), "/sock")
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}
