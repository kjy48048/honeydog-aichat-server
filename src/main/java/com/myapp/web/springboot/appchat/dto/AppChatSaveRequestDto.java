package com.myapp.web.springboot.appchat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 채팅 요청 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 19.
 *
 *     앱용 신버전 테스트중
 * </pre>
 */
@Data
@NoArgsConstructor
public class AppChatSaveRequestDto {
    private String messageType;  // 메세지 타입(메세지/AI채팅/시스템/타로...)
    private String aiUserUuid; // AI 채팅상대
    private String message; // 메세지
    private String nick; // 닉네임
    private String roomUuid; // 방 uuid
    private String userUuid; // 유저 uuid

    @Builder
    public AppChatSaveRequestDto(String message, String nick, String roomUuid, String userUuid, String messageType, String aiUserUuid) {
        this.message = message;
        this.nick = nick;
        this.roomUuid = roomUuid;
        this.userUuid = userUuid;
        this.messageType = messageType;
        this.aiUserUuid = aiUserUuid;
    }
}
