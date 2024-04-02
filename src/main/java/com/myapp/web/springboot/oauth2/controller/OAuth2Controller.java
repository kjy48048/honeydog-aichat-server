package com.myapp.web.springboot.oauth2.controller;

import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.oauth2.dto.AppAuthTokenResponseDto;
import com.myapp.web.springboot.oauth2.service.AppLoginService;
import com.myapp.web.springboot.oauth2.service.OAuth2UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <pre>
 *     설명: 앱유저 관리 OAuth2 인증 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 15.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/oauth2")
@Slf4j
public class OAuth2Controller {
    private final OAuth2UserService oAuth2UserService;
    private final AppLoginService appLoginService;

    @GetMapping("/google")
    public ResponseEntity<AppAuthTokenResponseDto> oauth2Google(@RequestHeader(value = "Authorization") String authorization) {
        log.info("=== google ===");
        String idToken;
        AppAuthTokenResponseDto tokenDto;
        try {
            if (authorization != null && authorization.startsWith("Bearer ")) {
                idToken = authorization.substring(7); // "Bearer " 다음부터의 문자열을 추출
            } else {
                log.info("oauth2Google invalid request");
                return ResponseEntity.badRequest().build();
            }
            AppUserResponseDto userResponseDto =  oAuth2UserService.findOrSaveMember(idToken, "google");
            tokenDto = appLoginService.saveTempToken(userResponseDto, idToken);
            return ResponseEntity.status(HttpStatus.OK).body(tokenDto);
        } catch (Exception e) {
            log.error("oauth2Google error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/google/test")
    public ResponseEntity<Map<String, Object>> oauth2GoogleTest(@RequestHeader(value = "Authorization") String authorization) {
        String idToken;
        if (authorization != null && authorization.startsWith("Bearer ")) {
            idToken = authorization.substring(7); // "Bearer " 다음부터의 문자열을 추출
        } else {
            return ResponseEntity.badRequest().build();
        }
        Map<String, Object> testMap = oAuth2UserService.getGoogleDataTest(idToken);
        return ResponseEntity.ok().body(testMap);
    }
}
