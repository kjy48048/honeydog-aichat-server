package com.myapp.web.springboot.oauth2.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class LogoutResponseDto {
    private String code;
    private String message;

    @Builder
    public LogoutResponseDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
