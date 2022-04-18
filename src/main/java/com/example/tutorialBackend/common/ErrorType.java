package com.example.tutorialBackend.common;

import java.util.HashMap;
import java.util.Map;

public enum ErrorType {
    SUCCESS(200, "성공"),

    //게시물 관련 에러
    NOT_EXIST_BOARD(600, "존재하지 않는 게시물입니다.")
    ;

    private int code;
    private String msg;

    ErrorType(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Map getInfo() {
        Map<String, String> result = new HashMap<>();
        result.put("code", String.valueOf(code));
        result.put("msg", msg);
        return result;
    }
}
