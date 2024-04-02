package com.myapp.web.springboot.approom.dto;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * <pre>
 *     설명: 앱 채팅방 저장용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@Data
@NoArgsConstructor
public class AppRoomRequestDto {
    private String roomUuid;
    private String roomNick;
    private String userUuid;
    private String friendEmails;
    private String aiUserUuid;
    private String roomStatus;
    private String roomType;

    /**
     * 저장용 Entity 변환
     * @return 방 Entity
     */
    public AppRoom toEntity() {
        return AppRoom.builder()
                .roomUuid(UUID.fromString(roomUuid))
                .roomNick(roomNick)
                .roomStatus(AppRoomStatus.fromString(roomStatus))
                .roomType(AppRoomType.fromString(roomType))
                .build();
    }
}
