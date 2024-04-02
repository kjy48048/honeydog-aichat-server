package com.myapp.web.springboot.oauth2.service;

import com.myapp.web.springboot.oauth2.domain.LoginHistory;
import com.myapp.web.springboot.oauth2.enums.LoginHistoryStatus;
import com.myapp.web.springboot.oauth2.enums.LoginResponseCode;
import com.myapp.web.springboot.oauth2.repository.LoginHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 *  <pre>
 *      설명: 앱용 로그인 저장 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 16.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class LoginHistoryService {
    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * 로그인 기록 저장
     * @return 저장성공여부
     */
    public void saveLoginHistory(String userUuid, String deviceId, LoginResponseCode code, LoginHistoryStatus status) {
        log.info("=== saveLoginHistory ===");
        if(LoginResponseCode.SUCCESS.equals(code)) {
            loginHistoryRepository.save(LoginHistory.builder()
                    .userUuid(userUuid)
                    .deviceId(deviceId)
                    .code(code.getCode())
                    .loginHistoryStatus(status)
                    .build());
        } else {
            loginHistoryRepository.save(LoginHistory.builder()
                    .userUuid(userUuid)
                    .deviceId(deviceId)
                    .code(code.getCode())
                    .errorMessage(code.getMessage())
                    .loginHistoryStatus(status)
                    .build());
        }
    }
}
