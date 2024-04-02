package com.myapp.web.springboot.appuserroom.controller;

import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomSaveRequestDto;
import com.myapp.web.springboot.appuserroom.dto.InviteUserRequestDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomService;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomSystemService;
import com.myapp.web.springboot.common.enums.ResponseCode;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     설명: 앱용 유저-채팅방 API 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v2/app/user-room")
public class AppUserRoomApiController {
    private final AppUserRoomService appUserRoomService;
    private final AppUserRoomSystemService appUserRoomSystemService;

    /**
     * 유저-채팅방 참여 혹은 퇴장 업데이트
     * @param userUuid 유저 UUID
     * @param roomUuid 채팅방 UUID
     * @param userRoomStatus 상태값
     * @return 유저-채팅방 응답 DTO
     */
    @PutMapping("/userUuid/{userUuid}/roomUuid/{roomUuid}/join/{userRoomStatus}")
    public ResponseEntity<AppUserRoomResponseDto> joinInOrOut(@PathVariable String userUuid, @PathVariable String roomUuid,
                                                              @PathVariable String userRoomStatus) {
        if (StringUtils.isEmpty(userRoomStatus)) {
            log.error("joinInOrOut fail... userRoomStatus is empty");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        AppUserRoomResponseDto appUserRoomResponseDto = appUserRoomService.findAppRoom(userUuid, roomUuid);
        UserRoomStatus status = UserRoomStatus.fromString(userRoomStatus);

        // 없는 경우 손님으로 저장
        if (appUserRoomResponseDto == null) {
            log.info("joinInOrOut new user joined... userUuid: {}, roomUuid: {} status: {}", userUuid, roomUuid, status);
            appUserRoomResponseDto = appUserRoomService.saveAppUserRoom(AppUserRoomSaveRequestDto.builder()
                    .appUserUuid(userUuid)
                    .appRoomUuid(roomUuid)
                    .userRoomStatus(status)
                    .userRoomRole(UserRoomRole.GUEST)
                    .build());
        } else {
            // 있는 경우 상태 업그레이드
            log.info("joinInOrOut user updated... userUuid: {}, roomUuid: {} status: {}", userUuid, roomUuid, status);
            appUserRoomResponseDto = appUserRoomService.updateUserRoomStatus(userUuid, roomUuid, status);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(appUserRoomResponseDto);
    }

    /**
     * 유저 초대
     * @param requestDto 유저 초대...
     * @return 유저-채팅방 응답 DTO
     */
    @PostMapping("/invite")
    public ResponseEntity<AppUserRoomResponseDto> inviteUser(@RequestBody InviteUserRequestDto requestDto) {
        AppUserRoomResponseDto responseDto = appUserRoomSystemService.inviteUser(requestDto);
        return ResponseCode.SUCCESS.equals(ResponseCode.fromString(responseDto.getRspCode()))
                ? ResponseEntity.status(HttpStatus.OK).body(responseDto)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }
}
