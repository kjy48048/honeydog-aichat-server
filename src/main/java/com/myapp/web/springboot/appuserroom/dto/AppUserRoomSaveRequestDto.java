package com.myapp.web.springboot.appuserroom.dto;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 유저-채팅방 관계 저장 요청 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 *
 *     앱용 신버전 테스트중
 * </pre>
 */
@Data
@NoArgsConstructor
public class AppUserRoomSaveRequestDto {
    private Long appUserRoomId;
    private String appUserUuid;
    private String appRoomUuid;
    private UserRoomRole userRoomRole;
    private UserRoomStatus userRoomStatus;

    @Builder
    public AppUserRoomSaveRequestDto(Long appUserRoomId, String appUserUuid, String appRoomUuid,
                                     UserRoomRole userRoomRole, UserRoomStatus userRoomStatus) {
        this.appUserRoomId = appUserRoomId;
        this.appUserUuid = appUserUuid;
        this.appRoomUuid = appRoomUuid;
        this.userRoomRole = userRoomRole;
        this.userRoomStatus = userRoomStatus;
    }

    public AppUserRoom toEntity(AppUser appUser, AppRoom appRoom) {
        return AppUserRoom.builder()
                .appUserRoomId(appUserRoomId)
                .appUser(appUser)
                .appRoom(appRoom)
                .userRoomRole(userRoomRole)
                .userRoomStatus(userRoomStatus)
                .build();
    }
}
