package com.myapp.web.springboot.approom.dto;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 *     설명: 앱 채팅방 화면 응답용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@Data
public class AppRoomResponseDto {
    private String roomUuid;
    private String roomNick;
    private AppRoomStatus roomStatus;
    private AppRoomType roomType;
    private String aiUserUuid;
    private LocalDateTime modifiedDate; // 수정일

    /**
     * 수정일 포맷팅
     * @return 포맷팅된 수정일
     */
    public String getFormattedModifiedDate() {
        return modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * Entity에서 응답용 DTO 변환
     * @param entity 채팅방 Entity
     */
    public AppRoomResponseDto(AppRoom entity) {
        if(entity.getRoomUuid() != null) {
            this.roomUuid = entity.getRoomUuid().toString();
        }
        this.roomNick = entity.getRoomNick();
        this.roomStatus = entity.getRoomStatus();
        this.roomType = entity.getRoomType();
        this.modifiedDate = entity.getModifiedDate();
    }
}
