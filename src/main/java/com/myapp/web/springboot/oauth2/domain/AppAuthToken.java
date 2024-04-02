package com.myapp.web.springboot.oauth2.domain;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.domain.BaseEntity;
import com.myapp.web.springboot.oauth2.enums.LoginHistoryStatus;
import com.myapp.web.springboot.oauth2.enums.TokenStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 *     설명: 앱용 로그인 인증토큰 도메인
 *     작성자: 김진영
 *     작성일: 2023. 11. 16.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AppAuthToken extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appAuthTokenId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID")
    private AppUser appUser; // 채팅방 유저

    @Column(nullable = false, columnDefinition = "TEXT")
    private String authToken;   // 토큰

    @Column(nullable = false)
    private String deviceId;    // 디바이스 아이디

    @Column(nullable = false)
    private LocalDateTime expiredDate;  // 만료일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenStatus tokenStatus;

    @Builder
    public AppAuthToken(AppUser appUser, String authToken, String deviceId, LocalDateTime expiredDate, TokenStatus tokenStatus) {
        this.appUser = appUser;
        this.authToken = authToken;
        this.deviceId = deviceId;
        this.expiredDate = expiredDate;
        this.tokenStatus = tokenStatus;
    }

    public AppAuthToken updateTempDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public AppAuthToken updateExpiredDate(LocalDateTime expiredDate) {
        this.expiredDate = expiredDate;
        return this;
    }

    public AppAuthToken updateExpired() {
        this.expiredDate = LocalDateTime.now();
        this.tokenStatus = TokenStatus.EXPIRED;
        return this;
    }
}
