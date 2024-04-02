package com.myapp.web.springboot.oauth2.dto;

import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.oauth2.domain.AppAuthToken;
import com.myapp.web.springboot.oauth2.enums.TokenStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppAuthTokenResponseDto {
    private Long appAuthTokenId;
    private AppUserResponseDto user; // user 정보
    private String authToken;   // 토큰
    private String deviceId;    // 디바이스 아이디
    private LocalDateTime expiredDate;  // 만료일
    private TokenStatus tokenStatus;

    private boolean isNewDevice; // 새로운기기여부

    public AppAuthTokenResponseDto(AppAuthToken appAuthToken) {
        this.appAuthTokenId = appAuthToken.getAppAuthTokenId();
        if(appAuthToken.getAppUser() != null) {
            this.user = new AppUserResponseDto(appAuthToken.getAppUser());
        }
        this.authToken = appAuthToken.getAuthToken();
        this.deviceId = appAuthToken.getDeviceId();
        this.expiredDate = appAuthToken.getExpiredDate();
        this.tokenStatus = appAuthToken.getTokenStatus();

        this.isNewDevice = false;
    }
}
