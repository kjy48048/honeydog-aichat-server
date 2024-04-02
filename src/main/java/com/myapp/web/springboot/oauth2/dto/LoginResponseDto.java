package com.myapp.web.springboot.oauth2.dto;

import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String code;
    private String message;
    private boolean isNewMember;
    private boolean isNewDevice;
    private String authToken;
    private AppUserResponseDto user;

    @Builder
    public LoginResponseDto(String code, String message, boolean isNewMember, boolean isNewDevice, String authToken, AppUserResponseDto user) {
        this.code = code;
        this.message = message;
        this.isNewMember = isNewMember;
        this.isNewDevice = isNewDevice;
        this.authToken = authToken;
        this.user = user;
    }
}
