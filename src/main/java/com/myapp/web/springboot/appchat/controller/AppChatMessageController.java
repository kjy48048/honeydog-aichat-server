package com.myapp.web.springboot.appchat.controller;

import com.myapp.web.springboot.appchat.dto.AppChatCallResponseDto;
import com.myapp.web.springboot.appchat.dto.AppChatSaveRequestDto;
import com.myapp.web.springboot.appchat.service.AppChatSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * <pre>
 *     설명: 앱용 채팅 메세지 관련 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 15.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class AppChatMessageController {

    private final AppChatSystemService appChatSystemService;

    /**
     * 요청 받은 채팅을 관리(버전2)
     * API 버전(POST)
     * @param requestDto 채팅 요청 DTO
     */
    @PostMapping("/api/v2/message")
    public AppChatCallResponseDto handleMessageV2(@RequestBody AppChatSaveRequestDto requestDto) throws InterruptedException, ExecutionException {
        CompletableFuture<AppChatCallResponseDto> resultFuture = appChatSystemService.messageHandleV2(requestDto);
        return resultFuture.get();
    }

    /**
     * 요청 받은 채팅을 관리(버전2)
     * Stomp 버전(메세지 매핑)
     * 앱에서 실사용 하는 것...
     * @param requestDto 채팅 요청 DTO
     */
    @MessageMapping("/chat/v2/message")
    public void messageV2(@RequestBody AppChatSaveRequestDto requestDto) {
        try {
            appChatSystemService.messageHandleV2(requestDto);
        } catch (Exception e) {
            log.error("messageHandle v2 error... requestDto: {}", requestDto, e);
            throw e;
        }
    }
}
