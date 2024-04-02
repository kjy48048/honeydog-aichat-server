package com.myapp.web.springboot.oauth2.controller;

import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.oauth2.dto.LoginRequestDto;
import com.myapp.web.springboot.oauth2.dto.LoginResponseDto;
import com.myapp.web.springboot.oauth2.dto.LogoutRequestDto;
import com.myapp.web.springboot.oauth2.dto.LogoutResponseDto;
import com.myapp.web.springboot.oauth2.enums.LoginHistoryStatus;
import com.myapp.web.springboot.oauth2.enums.LoginResponseCode;
import com.myapp.web.springboot.oauth2.service.AppLoginService;
import com.myapp.web.springboot.oauth2.service.LoginHistoryService;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     설명: 앱용 로그인 API
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 16.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v2/app/log")
public class AppLoginController {
    private final AppLoginService appLoginService;
    private final LoginHistoryService loginHistoryService;

    /**
     * 자동 로그인 처리
     * 이메일, 디바이스 아이디, 인증 토큰 DB 일치하는 데이터 확인 후 있으면 성공
     * @param requestDto 로그인 요청 DTO
     * @return 로그인 응답결과
     */
    @PostMapping("/in")
    public ResponseEntity<LoginResponseDto> login(@RequestHeader(value = "Authorization") String authorization,
                                                  @RequestBody LoginRequestDto requestDto) {
        // 인증토큰
        String authToken;

        // check validation...
        if(requestDto == null) {
            loginHistoryService.saveLoginHistory("", "", LoginResponseCode.ERROR_INVALID_REQUEST, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": requestDto is null")
                            .build());
        }

        // 디바이스 고유 아이디 체크
        if(StringUtils.isEmpty(requestDto.getDeviceId())) {
            loginHistoryService.saveLoginHistory("", requestDto.getDeviceId(),
                    LoginResponseCode.ERROR_INVALID_REQUEST, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": deviceId is empty")
                            .build());
        }

        if (authorization != null && authorization.startsWith("Bearer ")) {
            authToken = authorization.substring(7); // "Bearer " 다음부터의 문자열을 추출
        } else {
            loginHistoryService.saveLoginHistory("", requestDto.getDeviceId(),
                    LoginResponseCode.ERROR_INVALID_REQUEST, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": authToken is empty")
                            .build());
        }

        // 이메일 체크
        if(StringUtils.isEmpty(requestDto.getEmail())) {
            loginHistoryService.saveLoginHistory("", requestDto.getDeviceId(),
                    LoginResponseCode.ERROR_INVALID_REQUEST, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LoginResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": email is empty")
                            .build());
        }

        LoginResponseDto responseDto = appLoginService.login(authToken, requestDto);

        if(responseDto.getCode().equals(LoginResponseCode.SUCCESS.getCode())) {
            // 성공 로그인 이력 저장...
            loginHistoryService.saveLoginHistory(this.getUserOrEmpty(responseDto.getUser()), requestDto.getDeviceId(),
                    LoginResponseCode.SUCCESS, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(responseDto);
        } else if(responseDto.getCode().equals(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())) {
            loginHistoryService.saveLoginHistory(this.getUserOrEmpty(responseDto.getUser()), requestDto.getDeviceId(),
                    LoginResponseCode.ERROR_INVALID_REQUEST, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(responseDto);
        } else {
            loginHistoryService.saveLoginHistory(this.getUserOrEmpty(responseDto.getUser()), requestDto.getDeviceId(),
                    LoginResponseCode.ERROR_INTERNAL_SERVER_ERROR, LoginHistoryStatus.IN);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseDto);
        }
    }

    /**
     * 로그아웃 처리
     * @param requestDto 로그아웃 요청 DTO
     * @return 로그아웃 응답결과
     */
    @PostMapping("/out")
    public ResponseEntity<LogoutResponseDto> logout(@RequestHeader(value = "Authorization") String authorization,
                                                    @RequestBody LogoutRequestDto requestDto) {
        String authToken;
        // check validation...
        if(requestDto == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LogoutResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": requestDto is null")
                            .build());
        }

        if(StringUtils.isEmpty(requestDto.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LogoutResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": email is empty")
                            .build());
        }

        if (authorization != null && authorization.startsWith("Bearer ")) {
            authToken = authorization.substring(7); // "Bearer " 다음부터의 문자열을 추출
        } else {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LogoutResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": authToken is empty")
                            .build());
        }

        if(StringUtils.isEmpty(requestDto.getDeviceId())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(LogoutResponseDto.builder()
                            .code(LoginResponseCode.ERROR_INVALID_REQUEST.getCode())
                            .message(LoginResponseCode.ERROR_INVALID_REQUEST.getMessage() + ": deviceId is empty")
                            .build());
        }

        LogoutResponseDto responseDto = appLoginService.logout(authToken, requestDto);

        if(responseDto.getCode().equals(LoginResponseCode.SUCCESS.getCode())) {
            loginHistoryService.saveLoginHistory(requestDto.getUserUuid(), requestDto.getDeviceId(),
                    LoginResponseCode.SUCCESS, LoginHistoryStatus.OUT);
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(responseDto);
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(responseDto);
        }
    }

    private String getUserOrEmpty(AppUserResponseDto user) {
        return user != null ? user.getUserUuid() : "";
    }
}
