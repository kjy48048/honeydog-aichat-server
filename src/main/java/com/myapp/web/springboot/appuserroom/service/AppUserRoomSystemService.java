package com.myapp.web.springboot.appuserroom.service;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.approom.service.AppRoomService;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.dto.InviteUserRequestDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.common.enums.ResponseCode;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <pre>
 *     설명: 앱용 유저-채팅방 다대다(N:M) 관계 로직 처리 서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 20.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppUserRoomSystemService {
    private final AppUserRoomService appUserRoomService;
    private final AppUserService appUserService;
    private final AppRoomService appRoomService;

    public AppUserRoomResponseDto inviteUser(InviteUserRequestDto requestDto) {
        // valid check
        if (requestDto == null || StringUtils.isEmpty(requestDto.getUserUuid())
                || StringUtils.isEmpty(requestDto.getRoomUuid())
                || (StringUtils.isEmpty(requestDto.getInvitedUserEmail()) && StringUtils.isEmpty(requestDto.getInvitedAiUserUuid())) ) {
            log.error("inviteUser fail >>> some field is empty... requestDto: {}", requestDto);
            AppUserRoomResponseDto responseDto = new AppUserRoomResponseDto();
            responseDto.setRspCode(ResponseCode.FAIL.name());
            responseDto.setRspMsg("요청에 실패하였습니다.");
            return responseDto;
        }

        AppUserRoomResponseDto findAppRoom = appUserRoomService.findAppRoom(requestDto.getUserUuid(), requestDto.getRoomUuid());

        // 없는 경우 에러 처리...
        if (findAppRoom == null) {
            log.info("inviteUser fail >>> find app room fail... requestDto: {}", requestDto);
            AppUserRoomResponseDto responseDto = new AppUserRoomResponseDto();
            responseDto.setRspCode(ResponseCode.FAIL.name());
            responseDto.setRspMsg("요청에 실패하였습니다.");
            return responseDto;
        }

        // 있는 경우 처리...
        AppRoom appRoom = appRoomService.findById(requestDto.getRoomUuid());
        AppUser invitedUser;
        boolean isAi;
        if (requestDto.getInvitedUserEmail() != null) {
            invitedUser = appUserService.findByEmail(requestDto.getInvitedUserEmail());
            isAi = false;
        } else {
            invitedUser = appUserService.findById(requestDto.getInvitedAiUserUuid());
            isAi = true;
        }

        if (appRoom == null || invitedUser == null) {
            log.info("inviteUser fail >>> find room and invitedUser fail... requestDto: {}", requestDto);
            AppUserRoomResponseDto responseDto = new AppUserRoomResponseDto();
            responseDto.setRspCode(ResponseCode.FAIL.name());
            responseDto.setRspMsg("요청에 실패하였습니다.");
            return responseDto;
        }

        AppUserRoom newUserInRoom = appUserRoomService.save(AppUserRoom.builder()
                .appUser(invitedUser)
                .appRoom(appRoom)
                .userRoomRole(isAi ? UserRoomRole.AI : UserRoomRole.GUEST)
                .userRoomStatus(UserRoomStatus.IN)
                .inDateTime(LocalDateTime.now())
                .build());

        if(!isAi) {
            // 친구 방일 경우 그룹방으로 업데이트
            if(AppRoomType.FRIEND.equals(appRoom.getRoomType())) {
                appRoomService.updateAppRoomType(appRoom, AppRoomType.FRIEND_GROUP);
            }
        }

        AppUserRoomResponseDto responseDto = new AppUserRoomResponseDto(newUserInRoom);
        responseDto.setRspCode(ResponseCode.SUCCESS.name());
        return responseDto;
    }
}
