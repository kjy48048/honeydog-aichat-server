package com.myapp.web.springboot.appuserroom.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 유저 초대 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 20.
 *
 *     앱 채팅방 유저 초대 요청
 * </pre>
 */
@Data
@NoArgsConstructor
public class InviteUserRequestDto {
    private String userUuid;
    private String roomUuid;
    private String invitedUserEmail;    // 초대된 유저 이메일
    private String invitedAiUserUuid;   // 초대된 AI 유저

}
