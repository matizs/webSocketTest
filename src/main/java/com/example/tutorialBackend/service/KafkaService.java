package com.example.tutorialBackend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.example.tutorialBackend.dto.response.AlarmMessage;
import com.example.tutorialBackend.dto.response.AlarmResponse;
import com.example.tutorialBackend.enums.AlarmOpcode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaService {
    @Value("${kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();

    public void sendMessage(String message) {
        AlarmMessage alarmMessage = new AlarmMessage();
        alarmMessage.setData(message);
        alarmMessage.setIsRead(false);
        AlarmResponse res = AlarmResponse.builder().data(alarmMessage)
            .opcode(AlarmOpcode.SEND_ALARM.toString()).build();

        kafkaTemplate.send(topic, res);
    }
}
