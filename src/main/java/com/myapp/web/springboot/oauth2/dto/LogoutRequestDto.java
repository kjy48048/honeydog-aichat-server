package com.myapp.web.springboot.oauth2.dto;

import lombok.Data;

@Data
public class LogoutRequestDto {
    private String userUuid;
    private String deviceId;
    private String email;
}
