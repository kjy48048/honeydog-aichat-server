package com.myapp.web.springboot.appchat.dto;

import com.myapp.web.springboot.appchat.domain.AppChatHistory;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.common.utils.DateUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 *     설명: 앱용 채팅 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@Data
@NoArgsConstructor
public class AppChatResponseDto {
    private Long id;
    private String roomUuid;
    private String userUuid;
    private String nick;
    private String picture;

    private String messageType;
    private String message;
    private LocalDateTime modifiedDate; // 수정일
    private LocalDateTime createdDate; // 생성일

    private boolean isAi;

    /**
     * 포매팅된 수정일 조회용
     * @return 포매팅한 수정일
     */
    public String getFormattedCreatedDate() {
        return createdDate == null ? "" : DateUtils.dateParseForChatList(createdDate);
    }

    /**
     * 앱용 챗 Entity를 조회용 Dto로 변환
     * @param entity 채팅 응답용 DTO
     */
    public AppChatResponseDto(AppChatHistory entity) {
        if(entity.getAppRoom() != null) {
            this.roomUuid = entity.getAppRoom().getRoomUuid().toString();
        }
        if(entity.getAppUser() != null) {
            this.userUuid = entity.getAppUser().getUserUuid().toString();
            this.nick = entity.getAppUser().getNick();
            this.picture = entity.getAppUser().getPicture();

            if(AppUserRole.AI.equals(entity.getAppUser().getAppUserRole())) {
                this.isAi = true;
            }
        }
        this.id = entity.getAppChatHistoryId();
        this.messageType = entity.getMessageType().name();
        this.message = entity.getMessage();
        this.modifiedDate = entity.getModifiedDate();
        this.createdDate = entity.getCreatedDate();
    }
}
