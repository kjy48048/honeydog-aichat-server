package com.myapp.web.springboot.oauth2.domain;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.domain.BaseEntity;
import com.myapp.web.springboot.oauth2.enums.LoginHistoryStatus;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 로그인 히스토리 기록용 도메인
 *     작성자: 김진영
 *     작성일: 2023. 11. 16.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class LoginHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loginHistoryHistoryId;

    private String userUuid; // 채팅방 유저

    private String deviceId;    // 디바이스 아이디

    @Column(nullable = false)
    private String code; // 코드

    private String errorMessage; // 에러메세지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoginHistoryStatus loginHistoryStatus;
    @Builder
    public LoginHistory(String userUuid, String deviceId, String code, String errorMessage, LoginHistoryStatus loginHistoryStatus) {
        this.userUuid = userUuid;
        this.deviceId = deviceId;
        this.code = code;
        this.errorMessage = errorMessage;
        this.loginHistoryStatus = loginHistoryStatus;
    }
}
