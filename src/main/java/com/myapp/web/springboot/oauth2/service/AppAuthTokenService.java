package com.myapp.web.springboot.oauth2.service;

import com.myapp.web.springboot.oauth2.domain.AppAuthToken;
import com.myapp.web.springboot.oauth2.repository.AppAuthTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  <pre>
 *      설명: 앱용 유저 토큰 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 16.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppAuthTokenService {
    private final AppAuthTokenRepository appAuthTokenRepository;

    /**
     * 살아있는 디바이스 ID 일치하지 않는 인증토큰 전부 만료시키기
     * @param deviceId 디바이스 ID
     */
    public void updateExpiredTokensByDeviceId(String deviceId) {
        // exsitedTokens
        log.info("=== updateExpiredTokens ===");
        this.findAliveAppAuthTokenByDeviceId(deviceId)
                .forEach(AppAuthToken::updateExpired);
    }

    /**
     * 디바이스 ID로 살아있는 토큰 조회
     * @param deviceId 디바이스 ID
     * @return 인증토큰 목록
     */
    private List<AppAuthToken> findAliveAppAuthTokenByDeviceId(String deviceId) {
        return appAuthTokenRepository.findAliveAppAuthTokenByDeviceId(deviceId);
    }

    /**
     * 살아 있는 인증토큰으로 조회
     * @param authToken 인증토큰
     * @return 인증토큰 데이터
     */
    public AppAuthToken findAppAuthTokensByAuthToken(String authToken) {
        return appAuthTokenRepository.findAppAuthTokensByAuthToken(authToken)
                .stream().findFirst().orElseThrow(NullPointerException::new);
    }

    /**
     * 자동로그인용 살아있는 토큰 조회
     * @param authToken 인증토큰
     * @param deviceId 디바이스 아이디
     * @param email 이메일
     * @return 인증토큰 데이터
     */
    public AppAuthToken findAliveAppAuthTokenByAuthTokenAndDeviceIdAndEmail(String authToken, String deviceId, String email) {
        return appAuthTokenRepository.findAliveAppAuthTokensByAuthTokenAndDeviceIdAndEmail(authToken, deviceId, email)
                .stream().findFirst().orElse(null);
    }

    public AppAuthToken save(AppAuthToken appAuthToken) {
        return appAuthTokenRepository.save(appAuthToken);
    }
}
