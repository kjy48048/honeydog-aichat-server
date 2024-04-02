package com.myapp.web.springboot.appchat.controller;

import com.myapp.web.springboot.appchat.dto.AppChatResponseDto;
import com.myapp.web.springboot.appchat.service.AppChatHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 *     설명: 채팅 API
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class AppChatApiController {
    private final AppChatHistoryService appChatHistoryService;

    /**
     * 채팅방 아이디로 채팅목록 조회
     * @param roomUuid 방 UUID
     * @return 채팅목록
     */
    @GetMapping("/api/v2/app/chat/room")
    public ResponseEntity<List<AppChatResponseDto>> findAppChatList(@RequestParam String roomUuid, @RequestParam int page, @RequestParam int pageSize) {
        try {
            log.info("=== findAppChatList ===");
            Pageable pageable = PageRequest.of(page, pageSize);
            List<AppChatResponseDto> chatHistoryList = appChatHistoryService.findChatListByRoomUuid(roomUuid, pageable);

            for(AppChatResponseDto chatHistory : chatHistoryList) {
                log.info("findAppChat{}: {}", chatHistory.getRoomUuid() ,chatHistory);
            }
            return ResponseEntity.status(HttpStatus.OK).body(chatHistoryList);
        } catch (Exception e) {
            log.error("room save error... roomUuid: {}", roomUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 마지막 채팅 조회
     * @param roomUuid 방 UUID
     * @return 마지막 채팅 조회
     */
    @GetMapping("/api/v2/app/chat/room/last-chat")
    public ResponseEntity<AppChatResponseDto> findLastAppChat(@RequestParam String roomUuid) {
        try {
            log.info("=== findLastAppChat ===");
            Pageable pageable = PageRequest.of(0, 1);
            List<AppChatResponseDto> chatHistoryList = appChatHistoryService.findChatListByRoomUuid(roomUuid, pageable);

            for(AppChatResponseDto chatHistory : chatHistoryList) {
                log.info("findLastAppChat{}: {}", chatHistory.getRoomUuid() ,chatHistory);
            }
            return ResponseEntity.status(HttpStatus.OK).body(chatHistoryList.size() > 0 ? chatHistoryList.get(0) : new AppChatResponseDto());
        } catch (Exception e) {
            log.error("room save error... roomUuid: {}", roomUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new AppChatResponseDto());
        }
    }
}
