package com.myapp.web.springboot.appuserroom.service;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.repository.AppRoomRepository;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomSaveRequestDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.appuserroom.repository.AppUserRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 *     설명: 앱용 유저-채팅방 다대다(N:M) 관계 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppUserRoomService {
    private final AppUserRoomRepository appUserRoomRepository;
    private final AppUserRepository appUserRepository;
    private final AppRoomRepository appRoomRepository;

    /**
     * 채팅방 uuid로 유저-채팅방 관계 조회
     *
     * @param roomUuid 채팅방 Uuid
     * @return 채팅방에 존재하는 유저 목록
     */
    public List<AppUserRoomResponseDto> findJoinUsersInRoom(String roomUuid) {
        return appUserRoomRepository.findAppUserRoomsByRoomUuid(UUID.fromString(roomUuid), UserRoomStatus.IN)
                .stream().map(AppUserRoomResponseDto::new).collect(Collectors.toList());
    }

    /**
     * 유저 uuid로 유저-채팅방 관계 조회
     *
     * @param userUuid 유저 Uuid
     * @return 유저가 아직 참여하고 있는 채팅방 목록
     */
    public List<AppUserRoom> findJoinRoomsByUser(String userUuid) {
        return appUserRoomRepository.findAppUserRoomsByUserUuidAndRoomStatus(UUID.fromString(userUuid), UserRoomStatus.IN);
    }

    public AppUserRoom save(AppUserRoom appUserRoom) {
        return appUserRoomRepository.save(appUserRoom);
    }

    /**
     * 유저 uuid로 유저-채팅방 관계 조회
     *
     * @param userUuid 유저 Uuid
     * @param userUuid 유저 Uuid
     * @return 유저가 아직 참여하고 있는 채팅방 목록
     */
    public AppUserRoomResponseDto findAppRoom(String userUuid, String roomUuid) {
        return new AppUserRoomResponseDto(appUserRoomRepository
                .findAppUserRoomByRoomUuidAndUserUuid(UUID.fromString(userUuid), UUID.fromString(roomUuid)));
    }

    /**
     * 유저-채팅방 관계 저장
     *
     * @param requestDto 유저-채팅방 관계저장요청 Dto
     * @return 유저-채팅방 관계 응답 Dto
     */
    public AppUserRoomResponseDto saveAppUserRoom(AppUserRoomSaveRequestDto requestDto) {
        UUID userUuid = UUID.fromString(requestDto.getAppUserUuid());
        UUID roomUuid = UUID.fromString(requestDto.getAppRoomUuid());

        AppUser appUser = appUserRepository.findByUserUuid(userUuid);
        AppRoom appRoom = appRoomRepository.findAppRoomByRoomUuidAndRoomStatus(roomUuid, AppRoomStatus.OPEN);

        if (appUser == null || appRoom == null) {
            log.error("appUser or appRoom find but fail... requestDto: [{}], appUser: [{}], appRoom: [{}]", requestDto, appUser, appRoom);
            throw new IllegalArgumentException();
        }

        AppUserRoom appUserRoom = appUserRoomRepository.save(AppUserRoom.builder()
                .appUser(appUser)
                .appRoom(appRoom)
                .userRoomRole(requestDto.getUserRoomRole())
                .userRoomStatus(UserRoomStatus.IN)
                .inDateTime(LocalDateTime.now())
                .build());

        return new AppUserRoomResponseDto(appUserRoom);
    }

    /**
     * In/Out 업데이트
     * @param userUuid 유저 UUID
     * @param roomUuid 방 UUID
     * @param userRoomStatus 들어오고 나가고 상태
     * @return 응답 DTO
     */
    @Transactional
    public AppUserRoomResponseDto updateUserRoomStatus(String userUuid, String roomUuid, UserRoomStatus userRoomStatus) {
        AppUserRoom appUserRoom = appUserRoomRepository
                .findAppUserRoomByRoomUuidAndUserUuid(UUID.fromString(userUuid), UUID.fromString(roomUuid));

        return UserRoomStatus.IN.equals(userRoomStatus)
                ? new AppUserRoomResponseDto(appUserRoom.inRoom())
                : new AppUserRoomResponseDto(appUserRoom.outRoom());
    }

    /**
     * 채팅방 유저 찾기 or 저장
     * @param userUuid 유저 ID
     * @param roomUuid 방 ID
     * @return 채팅방 유저 응답 Dto
     */
    @Transactional
    public AppUserRoomResponseDto findOrSave(UUID userUuid, UUID roomUuid) {
        AppUserRoom findUserInRoom = appUserRoomRepository.findAppUserRoomByRoomUuidAndUserUuid(userUuid, roomUuid);
        AppUserRoomResponseDto userInRoomDto;
        if(findUserInRoom != null) {
            userInRoomDto = new AppUserRoomResponseDto(findUserInRoom);
        } else {
            AppUser appUser = appUserRepository.findByUserUuid(userUuid);
            AppRoom appRoom = appRoomRepository.findAppRoomByRoomUuidAndRoomStatus(roomUuid, AppRoomStatus.OPEN);

            AppUserRoom newGuestInRoom = appUserRoomRepository.save(AppUserRoom.builder()
                    .appUser(appUser)
                    .appRoom(appRoom)
                    .userRoomRole(UserRoomRole.GUEST)
                    .userRoomStatus(UserRoomStatus.IN)
                    .inDateTime(LocalDateTime.now())
                    .build());

            userInRoomDto = new AppUserRoomResponseDto(newGuestInRoom);
        }
        return userInRoomDto;
    }

    /**
     * 유저-방 아이디로 찾기
     * @param appUserRoomId 유저-방 아이디
     * @return
     */
    public AppUserRoom findById(Long appUserRoomId) {
        return appUserRoomRepository.findById(appUserRoomId).orElse(null);
    }

    /**
     * 방 아이디로 ai 유저 찾기
     * @param roomUuid 방 아이디
     * @return
     */
    public AppUserRoom findAiUserRoomByRoomUuid(UUID roomUuid) {
        List<AppUserRoom> aiUserRoomList = appUserRoomRepository.findAiUsersRoomByRoomUuid(roomUuid);
        return aiUserRoomList != null && aiUserRoomList.size() > 0 ? aiUserRoomList.get(0) : null;
    }
}
