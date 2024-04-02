package com.myapp.web.springboot.oauth2.service;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.oauth2.domain.AppAuthToken;
import com.myapp.web.springboot.oauth2.dto.*;
import com.myapp.web.springboot.oauth2.enums.LoginResponseCode;
import com.myapp.web.springboot.oauth2.enums.TokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 *  <pre>
 *      설명: 앱용 로그인, 로그아웃 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 16.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppLoginService {
    private final AppUserService appUserService;
    private final AppAuthTokenService appAuthTokenService;
    /**
     * 앱 유저용 로그인처리
     * @param requestDto 로그아웃 요청 Dto
     * @return 로그인 응답 Dto
     */
    public LoginResponseDto login(String authToken, LoginRequestDto requestDto) {
        log.info("=== login ===");
        log.info("requestDto: {}", requestDto);
        try {
            AppAuthTokenResponseDto tokenDto = this.findAppAuthToken(authToken, requestDto);
            if(tokenDto == null) {
                // 로그인 실패
                log.info("appUser find fail...");
                return LoginResponseDto.builder()
                        .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                        .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage())
                        .build();
            }

            // 로그인 성공
            return LoginResponseDto.builder()
                    .code(LoginResponseCode.SUCCESS.getCode())
                    .message(LoginResponseCode.SUCCESS.getMessage())
                    .isNewMember(false)
                    .isNewDevice(tokenDto.isNewDevice())
                    .authToken(tokenDto.getAuthToken())
                    .user(tokenDto.getUser())
                    .build();
        } catch (Exception e) {
            // 로그인 에러
            log.error("login error... e: {}", e.getMessage(), e);
            return LoginResponseDto.builder()
                    .code(LoginResponseCode.ERROR_INTERNAL_SERVER_ERROR.getCode())
                    .message(LoginResponseCode.ERROR_INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * 앱 유저용 로그아웃처리
     * @param requestDto 로그아웃 요청 Dto
     * @return 로그아웃 응답 Dto
     */
    public LogoutResponseDto logout(String authToken, LogoutRequestDto requestDto) {
        try {
            // 일치하는 토큰 있는지 여부 체크
            AppAuthToken appAuthToken = appAuthTokenService.findAppAuthTokensByAuthToken(authToken);
            if(appAuthToken != null) {
                log.info("logout token expired process... deviceId: {}", requestDto.getDeviceId());
                appAuthTokenService.updateExpiredTokensByDeviceId(requestDto.getDeviceId());
            } else {
                log.info("logout token not find... requestDto: {}", requestDto);
                return LogoutResponseDto.builder()
                        .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                        .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": logout token not find")
                        .build();
            }

            return LogoutResponseDto.builder()
                    .code(LoginResponseCode.SUCCESS.getCode())
                    .message(LoginResponseCode.SUCCESS.getMessage())
                    .build();
        } catch (Exception e) {
            log.error("logout error... e: {}", e.getMessage(), e);
            return LogoutResponseDto.builder()
                    .code(LoginResponseCode.ERROR_INTERNAL_SERVER_ERROR.getCode())
                    .message(LoginResponseCode.ERROR_INTERNAL_SERVER_ERROR.getMessage() + ": " + e.getMessage())
                    .build();
        }
    }

    /**
     * 임시토큰 저장...
     * @param userResponseDto 유저 DTO
     * @param idToken 토큰
     * @return 토큰 응답 DTO
     */
    public AppAuthTokenResponseDto saveTempToken(AppUserResponseDto userResponseDto, String idToken) {
        log.info("=== saveToken ===");

        // 토큰 저장...
        log.info("appAuthToken find null and save...");
        AppAuthToken appAuthToken = appAuthTokenService.save(AppAuthToken.builder()
                .appUser(appUserService.findByEmail(userResponseDto.getEmail()))
                .authToken(idToken)
                .deviceId("TEMP")
                .expiredDate(LocalDateTime.now().plusHours(6))
                .tokenStatus(TokenStatus.ALIVE)
                .build());

        return new AppAuthTokenResponseDto(appAuthToken);
    }

    /**
     * AppAuthToken 조회...
     * @param requestDto 자동 로그인 조회 요청 DTO
     * @return 토큰 응답 DTOs
     */
    private AppAuthTokenResponseDto findAppAuthToken(String authToken, LoginRequestDto requestDto) {
        AppAuthTokenResponseDto responseDto;

        // 기존 토큰 있는지 체크
        AppAuthToken appAuthToken = appAuthTokenService.findAliveAppAuthTokenByAuthTokenAndDeviceIdAndEmail(authToken, requestDto.getDeviceId(), requestDto.getEmail());
        if(appAuthToken == null) {
            // 없으면 널 리턴
            log.info("=== appAuthToken can't find ===");
            return null;
        } else {
            // 있으면 디바이스 아이디가 임시인지 체크...=== login ===
            if("TEMP".equals(appAuthToken.getDeviceId())) {
                // 같지만 임시 디바이스인 경우
                log.info("=== appAuthToken find tempDevice ===");
                appAuthToken.updateTempDeviceId(requestDto.getDeviceId());
                appAuthToken.updateExpiredDate(LocalDateTime.now().plusHours(6));
                responseDto = new AppAuthTokenResponseDto(appAuthToken);

                responseDto.setNewDevice(true);
            } else {
                // 같으면 리턴
                log.info("=== appAuthToken find ===");
                appAuthToken.updateExpiredDate(LocalDateTime.now().plusHours(6));
                responseDto = new AppAuthTokenResponseDto(appAuthToken);
            }
        }
        return responseDto;
    }

    /**
     * AppAuthToken 조회 혹은 저장...
     * @param requestDto 로그인 조회 요청 DTO
     * @return 토큰 응답 DTO
     */
    private AppAuthTokenResponseDto findOrSaveAppAuthToken(String authToken, LoginRequestDto requestDto) {
        AppAuthTokenResponseDto responseDto;

        // 기존 이메일 있는지 체크
        AppUser user = appUserService.findByEmail(requestDto.getEmail());
        if(user == null) {
            return null;
        }

        // 기존 토큰 있는지 체크
        AppAuthToken appAuthToken = appAuthTokenService.findAliveAppAuthTokenByAuthTokenAndDeviceIdAndEmail(authToken, requestDto.getDeviceId(), requestDto.getEmail());
        if(appAuthToken == null) {
            // 없으면 신규 저장
            log.info("appAuthToken find null and save...");
            appAuthToken = appAuthTokenService.save(AppAuthToken.builder()
                    .appUser(user)
                    .authToken(authToken)
                    .deviceId(requestDto.getDeviceId())
                    .expiredDate(LocalDateTime.now().plusDays(1))
                    .tokenStatus(TokenStatus.ALIVE)
                    .build());
            responseDto = new AppAuthTokenResponseDto(appAuthToken);
            responseDto.setNewDevice(true);
        } else {
            // 있으면 디바이스 아이디도 같은지 체크...
            if(requestDto.getDeviceId().equals(appAuthToken.getDeviceId())) {
                // 같으면 리턴
                log.info("appAuthToken find...");
                appAuthToken.updateExpiredDate(LocalDateTime.now().plusDays(1));
                responseDto = new AppAuthTokenResponseDto(appAuthToken);
            } else if("TEMP".equals(appAuthToken.getDeviceId())) {
                // 같지만 임시 디바이스인 경우
                log.info("appAuthToken find tempDevice...");
                appAuthToken.updateTempDeviceId(appAuthToken.getDeviceId());
                appAuthToken.updateExpiredDate(LocalDateTime.now().plusDays(1));
                responseDto = new AppAuthTokenResponseDto(appAuthToken);

                responseDto.setNewDevice(true);
            } else {
                // 없으면 저장
                log.info("appAuthToken find but differ deviceId and save...");
                appAuthToken = appAuthTokenService.save(AppAuthToken.builder()
                        .appUser(user)
                        .authToken(authToken)
                        .deviceId(requestDto.getDeviceId())
                        .expiredDate(LocalDateTime.now().plusDays(1))
                        .tokenStatus(TokenStatus.ALIVE)
                        .build());

                responseDto = new AppAuthTokenResponseDto(appAuthToken);
                responseDto.setNewDevice(true);
            }
        }
        return responseDto;
    }
}
