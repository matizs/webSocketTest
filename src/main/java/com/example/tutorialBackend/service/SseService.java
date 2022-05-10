package com.example.tutorialBackend.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.tutorialBackend.dto.response.AlarmMessage;
import com.example.tutorialBackend.dto.response.AlarmResponse;
import com.example.tutorialBackend.enums.AlarmOpcode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SseService {
    public Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private ObjectMapper objectMapper = new ObjectMapper();
    public void addUser(String id, SseEmitter sseEmitter) {
        emitterMap.put(id, sseEmitter);
        try {
            AlarmMessage alarmMessage = new AlarmMessage();
            alarmMessage.setIsRead(false);
            alarmMessage.setData(id +" Connected");
            AlarmResponse reponse = AlarmResponse.builder().opcode(AlarmOpcode.CONNECT.toString())
                    .data(alarmMessage).build();
            sseEmitter.send(SseEmitter.event().reconnectTime(500).data(objectMapper.writeValueAsString(reponse)));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("{} 아이디에서 문제 발생",id);
            sseEmitter.complete();
            removeUser(id);
        }
    }

    public void broadCast(String message) {
        emitterMap.forEach((id, value)->{
            try {
                value.send(SseEmitter.event().reconnectTime(500).data(message, MediaType.APPLICATION_JSON));
            } catch (Exception e) {
                e.printStackTrace();
                log.error("{} 아이디에서 문제 발생",id);
                removeUser(id);
                try {
                    value.complete();
                }catch (Exception ee) {
                    ee.printStackTrace();
                }
            }
        });
    }

    private void removeUser(String id) {
        emitterMap.remove(id);
    }

}
