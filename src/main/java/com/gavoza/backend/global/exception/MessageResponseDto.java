package com.gavoza.backend.global.exception;

import lombok.Getter;

@Getter
public class MessageResponseDto {
    private String msg;

    public MessageResponseDto(String msg){
        this.msg = msg;
    }
}
