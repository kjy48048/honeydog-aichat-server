package com.myapp.web.springboot.openchat.controller;

import com.myapp.web.springboot.config.auth.LoginUser;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import com.myapp.web.springboot.openchat.service.OpenChatApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 *     설명: 오픈AI API 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class OpenChatApiController {
    private final OpenChatApiService openChatApiService;

    /**
     * 오픈AI 메세지 요청
     * @param message 전달할 메세지
     * @param user 세션 유저
     * @return 응답받은 메세지
     */
    @PostMapping("/api/v1/open/chat")
    public ResponseEntity<String> openApiChat(@RequestBody String message, @LoginUser SessionUser user) {
        try {
            String returnMessage = openChatApiService.sendMessage(message, user!=null ? user.getName() : "");
            return StringUtils.hasText(returnMessage)
                    ? ResponseEntity.status(HttpStatus.OK).body(returnMessage)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("openApiChat error... message: {}, user: {}", message, user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 오픈AI 이미지 요청
     * @param message 전달할 메세지
     * @param user 세션 유저
     * @return 응답받은 이미지 URL
     */
    @PostMapping("/api/v1/open/image")
    public ResponseEntity<String> openApiImage(@RequestBody String message, @LoginUser SessionUser user) {
        try {
            String returnImage = openChatApiService.findImageLink(message, user!=null ? user.getName() : "");
            return StringUtils.hasText(returnImage)
                    ? ResponseEntity.status(HttpStatus.OK).body(returnImage)
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            log.error("openApiImage error... message: {}, user: {}", message, user);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
