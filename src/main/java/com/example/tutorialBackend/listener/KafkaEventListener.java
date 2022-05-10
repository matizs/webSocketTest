package com.example.tutorialBackend.listener;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;

import com.example.tutorialBackend.dto.response.AlarmResponse;
import com.example.tutorialBackend.service.SseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class KafkaEventListener {

    private final SseService sseService;
    private ObjectMapper objectMapper = new ObjectMapper();
    @KafkaListener(id="alarmListener", topics = "${kafka.topic}", groupId = "${kafka.group}")
    public void receive(ConsumerRecord<String, Object> record) throws JsonProcessingException {
        sseService.broadCast(record.value().toString());
    }
}
