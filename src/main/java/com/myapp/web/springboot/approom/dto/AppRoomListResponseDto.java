package com.myapp.web.springboot.approom.dto;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.common.utils.DateUtils;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <pre>
 *     설명: 앱 채팅방 목록 응답용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 01.
 * </pre>
 */
@Data
public class AppRoomListResponseDto {
    private String roomUuid;
    private AppRoomStatus roomStatus;
    private AppRoomType roomType;
    private String roomNick;
    private String aiUserUuid;
    private String picture;
    private String lastMessage;
    private LocalDateTime modifiedDate; // 수정일

    /**
     * 날짜 형식에 맞춘 수정일
     * @return 날짜 형식에 맞춘 수정일
     */
    public String getFormattedModifiedDate() {
        return modifiedDate == null ? "" : DateUtils.dateParseForRoomList(modifiedDate);
    }

    /**
     * Entity에서 응답용 DTO 변환
     * @param entity 채팅방 Entity
     */
    public AppRoomListResponseDto(AppRoom entity) {
        if(entity.getRoomUuid() != null) {
            this.roomUuid = entity.getRoomUuid().toString();
        }
        this.roomStatus = entity.getRoomStatus();
        this.roomType = entity.getRoomType();
        this.roomNick = entity.getRoomNick();
        this.modifiedDate = entity.getModifiedDate();
    }
}
