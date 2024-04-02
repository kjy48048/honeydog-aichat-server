package com.myapp.web.springboot.oauth2.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String deviceId;
    private String email;
}
