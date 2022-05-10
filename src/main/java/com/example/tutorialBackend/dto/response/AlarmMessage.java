package com.example.tutorialBackend.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AlarmMessage {
    String data;
    Boolean isRead;
}
