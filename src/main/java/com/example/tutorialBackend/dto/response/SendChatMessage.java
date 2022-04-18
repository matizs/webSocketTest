package com.example.tutorialBackend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendChatMessage {
    private String opcode;
    private String payload;
}
