package com.myapp.web.springboot.appuserroom.dto;

import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 *     설명: 앱용 유저-채팅방 관계 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@Data
@NoArgsConstructor
public class AppUserRoomResponseDto extends CommonResponseDto {
    private Long appUserRoomId;
    private String appUserUuid;
    private String appRoomUuid;
    private UserRoomRole userRoomRole;
    private UserRoomStatus userRoomStatus;
    private LocalDateTime inDateTime; // 들어온 시간
    private LocalDateTime outDateTime; // 나간 시간
    private LocalDateTime modifiedDate; // 수정일

    /**
     * 포매팅된 들어온 시간 조회용
     * @return 포매팅한 수정일
     */
    public String getFormattedInDateTime() {
        return inDateTime == null ? "" : inDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 포매팅된 나간 시간 조회용
     * @return 포매팅한 수정일
     */
    public String getFormattedOutDateTime() {
        return outDateTime == null ? "" : outDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    /**
     * 포매팅된 수정일 조회용
     * @return 포매팅한 수정일
     */
    public String getFormattedModifiedDate() {
        return modifiedDate == null ? "" : modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public AppUserRoomResponseDto(AppUserRoom appUserRoom) {
        this.appUserRoomId = appUserRoom.getAppUserRoomId();
        if(appUserRoom.getAppUser() != null) {
            this.appUserUuid = appUserRoom.getAppUser().getUserUuid().toString();
        }
        if(appUserRoom.getAppRoom() != null) {
            this.appRoomUuid = appUserRoom.getAppRoom().getRoomUuid().toString();
        }
        this.userRoomRole = appUserRoom.getUserRoomRole();
        this.userRoomStatus = appUserRoom.getUserRoomStatus();
        this.inDateTime = appUserRoom.getInDateTime();
        this.outDateTime = appUserRoom.getOutDateTime();
        this.modifiedDate = appUserRoom.getModifiedDate();
    }
}
