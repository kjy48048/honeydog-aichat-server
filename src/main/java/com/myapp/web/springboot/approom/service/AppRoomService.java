package com.myapp.web.springboot.approom.service;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.dto.*;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.approom.repository.AppRoomRepository;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomSaveRequestDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomService;
import com.myapp.web.springboot.common.GenericSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * <pre>
 *     설명: 앱용 채팅방 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppRoomService {
    private final AppUserRoomService appUserRoomService;
    private final AppRoomRepository appRoomRepository;
    private final AppUserService appUserService;

    @Transactional
    public AppRoomResponseDto createRoom(AppRoomRequestDto requestDto) {
        // 채팅방 생성
        AppRoomResponseDto responseDto = this.save(requestDto);

        // 채팅방 생성 성공시 처리...
        // 1. 생성한 유저 본인 호스트로 채팅방 유저 생성
        appUserRoomService.saveAppUserRoom(AppUserRoomSaveRequestDto.builder()
                .appUserUuid(requestDto.getUserUuid())
                .appRoomUuid(responseDto.getRoomUuid())
                .userRoomStatus(UserRoomStatus.IN)
                .userRoomRole(UserRoomRole.HOST)
                .build());

        // 2. 채팅방이 친구방 일때 채팅방 친구 유저 생성
        AppRoomType requestedRoomType = AppRoomType.fromString(requestDto.getRoomType());
        if(requestedRoomType != null && requestedRoomType.equals(AppRoomType.FRIEND) && !StringUtils.isNullOrEmpty(requestDto.getFriendEmails())) {
            log.info("createRoom guest friend save start!");
            String[] friendEmails = requestDto.getFriendEmails().split(",");
            for(String friendEmail : friendEmails) {
                friendEmail = friendEmail.trim();
                log.info("createRoom guest friend email: {}", friendEmail);

                AppUser friendUser = appUserService.findByEmail(friendEmail);

                if(friendUser != null) {
                    appUserRoomService.saveAppUserRoom(AppUserRoomSaveRequestDto.builder()
                            .appUserUuid(friendUser.getUserUuid().toString())
                            .appRoomUuid(responseDto.getRoomUuid())
                            .userRoomStatus(UserRoomStatus.IN)
                            .userRoomRole(UserRoomRole.GUEST)
                            .build());
                }
            }
        } else if(requestedRoomType != null && requestedRoomType.equals(AppRoomType.AI)) {
            // 3. 채팅방이 AI방 일때 채팅방 AI 유저 생성
            log.info("=======createRoom AI friend start!==========");
            if(!StringUtils.isNullOrEmpty(requestDto.getAiUserUuid())) {
                AppUser findAiUser = appUserService.findById(requestDto.getAiUserUuid());
                log.info("findAiUser: {}", findAiUser);
                appUserRoomService.saveAppUserRoom(AppUserRoomSaveRequestDto.builder()
                        .appUserUuid(findAiUser.getUserUuid().toString())
                        .appRoomUuid(responseDto.getRoomUuid())
                        .userRoomStatus(UserRoomStatus.IN)
                        .userRoomRole(UserRoomRole.AI)
                        .build());

                // AI 채터 세팅
                responseDto.setAiUserUuid(requestDto.getAiUserUuid());
            }
        }

        return responseDto;
    }

    /**
     * 방타입이 (AI or FRIEND)인 경우, 두 유저의 최근 대화 방 찾기
     * @param requestDto 채팅방 요청 DTO
     * @return 채팅방 응답 DTO
     */
    public AppRoomResponseDto findRecentRoom(AppRoomRequestDto requestDto) {
        AppUser appUser = appUserService.findById(requestDto.getUserUuid());
        AppRoomType requestAppRoomType = AppRoomType.fromString(requestDto.getRoomType());
        AppUser anotherUser;

        if(requestAppRoomType != null) {
            if(requestAppRoomType.equals(AppRoomType.FRIEND)) {
                String friendEmail = requestDto.getFriendEmails().split(",")[0].trim();
                anotherUser = appUserService.findByEmail(friendEmail);
            } else if (requestAppRoomType.equals(AppRoomType.AI)) {
                anotherUser = appUserService.findById(requestDto.getAiUserUuid());
            } else {
                return null;
            }
            List<AppRoom> appRoomList = appRoomRepository.findRoomsWithTwoUsers(appUser.getUserUuid(), anotherUser.getUserUuid());
            if(appRoomList != null && appRoomList.size() > 0) {
                return new AppRoomResponseDto(appRoomList.get(0));
            }
        }
        return null;
    }

    /**
     * 앱용 채팅방 생성
     * @param requestDto 요청DTO
     * @return 채팅방 UUID
     */
    private AppRoomResponseDto save(AppRoomRequestDto requestDto) {
        AppRoom savedAppRoom = AppRoom.builder()
                .roomNick(requestDto.getRoomNick())
                .roomStatus(AppRoomStatus.OPEN)
                .roomType(AppRoomType.fromString(requestDto.getRoomType()))
                .build();
        return new AppRoomResponseDto(appRoomRepository.save(savedAppRoom));
    }

    /**
     * 앱용 채팅방 닉네임변경
     * @param requestDto 요청DTO
     * @return 채팅방 UUID
     */
    @Transactional
    public AppRoomResponseDto updateRoomNick(AppRoomRequestDto requestDto) {
        log.info("updateRoomNick > roomUuid : {}, updateNick: {}", requestDto.getRoomUuid(), requestDto.getRoomNick());
        AppRoom appRoom = appRoomRepository.findById(UUID.fromString(requestDto.getRoomUuid())).orElse(null);
        if(appRoom != null) {
            appRoom.updateRoomNick(requestDto.getRoomNick());
            appRoomRepository.save(appRoom);
        } else {
            log.error("closeRoom findAppRoomByRoomUuidAndRoomStatus, but null... roomUuid: {}", requestDto.getRoomUuid());
            throw new IllegalArgumentException();
        }
        return new AppRoomResponseDto(appRoom);
    }

    public AppRoomResponseDto findRoom(String roomUuid) {
        AppRoom appRoom = appRoomRepository.findAppRoomByRoomUuidAndRoomStatus(UUID.fromString(roomUuid), AppRoomStatus.OPEN);
        if(appRoom == null) return null;
        AppRoomResponseDto responseDto = new AppRoomResponseDto(appRoom);

        // AI방인 경우 AI 채터 설정
        if(AppRoomType.AI.equals(responseDto.getRoomType())) {
            List<AppUserRoomResponseDto> findJoinUsersInRoom = appUserRoomService.findJoinUsersInRoom(roomUuid);

            AppUserRoomResponseDto aiUserRoomDto = findJoinUsersInRoom
                    .stream()
                    .filter(user -> UserRoomRole.AI.equals(user.getUserRoomRole()))
                    .findFirst()
                    .orElse(null);

            if(aiUserRoomDto != null) {
                AppUserResponseDto aiUserDto = appUserService.findByUserUuid(aiUserRoomDto.getAppUserUuid());
                responseDto.setAiUserUuid(aiUserDto.getUserUuid());
            }
        }

        return responseDto;
    }

    public AppRoom findById(String roomUuid) {
        return appRoomRepository.findById(UUID.fromString(roomUuid)).orElse(null);
    }

    @Transactional
    public AppRoomResponseDto closeRoom(String roomUuid) {
        AppRoom appRoom = appRoomRepository.findAppRoomByRoomUuidAndRoomStatus(UUID.fromString(roomUuid), AppRoomStatus.OPEN);

        if(appRoom != null) {
            appRoom.updateRoomStatus(AppRoomStatus.CLOSED);
            appRoomRepository.save(appRoom);
        } else {
            log.error("closeRoom findAppRoomByRoomUuidAndRoomStatus, but null... roomUuid: {}", roomUuid);
            throw new IllegalArgumentException();
        }
        return new AppRoomResponseDto(appRoom);
    }

    /**
     * 유저 UUID로 참여한 채팅방 목록 조회
     * @param userUuid 유저 UUID
     * @return 참여한 채팅방 목록
     */
    public List<AppRoomListResponseDto> findJoinRooms(String userUuid) {
        return appRoomRepository.findJoinRooms(UUID.fromString(userUuid), AppRoomStatus.OPEN, UserRoomStatus.IN)
                .stream()
                .map(AppRoomListResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 관리자용 웹에서 채팅방 목록 조회
     * @param requestDto 요청 DTO
     * @return 검색된 채팅방 목록
     */
    public WebRoomListResponse findByWebRoomRequest(WebRoomRequestDto requestDto) {
        int pageNumber;
        int pageSize;
        try {
            pageNumber = StringUtils.isNumber(requestDto.getPageNumber()) ? Integer.parseInt(requestDto.getPageNumber()) : 0;
            pageSize = StringUtils.isNumber(requestDto.getPageSize()) ? Integer.parseInt(requestDto.getPageSize()) : 10;
        } catch (Exception e) {
            log.error("findByWebRoomRequest number format error... e: {}", e.getMessage(), e);
            pageNumber = 0;
            pageSize = 10;
        }

        Specification<AppRoom> spec = Specification.where(null);

        if(!StringUtils.isNullOrEmpty(requestDto.getRoomNick())) {
            spec.and(GenericSpecifications.like("roomNick", requestDto.getRoomNick()));
        }

        if(!StringUtils.isNullOrEmpty(requestDto.getRoomStatus())) {
            spec.and(GenericSpecifications.equal("roomStatus", requestDto.getRoomStatus()));
        }

        if(!StringUtils.isNullOrEmpty(requestDto.getRoomType())) {
            spec.and(GenericSpecifications.equal("roomType", requestDto.getRoomType()));
        }

        WebRoomListResponse webRoomListResponse = new WebRoomListResponse();
        Page<AppRoom> appRoomPage = appRoomRepository.findAll(spec, PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.DESC, "modifiedDate")));

        List<AppRoomResponseDto> appRoomResponseDtoList = appRoomPage
                                                        .stream()
                                                        .map(AppRoomResponseDto::new)
                                                        .collect(Collectors.toList());

        webRoomListResponse.setRoomList(appRoomResponseDtoList);
        webRoomListResponse.setTotalPages(appRoomPage.getTotalPages());
        webRoomListResponse.setPageNumber(appRoomPage.getNumber());

        return webRoomListResponse;
    }

    public AppRoom updateAppRoomType(AppRoom appRoom, AppRoomType appRoomType) {
        appRoom.updateRoomType(appRoomType);
        return appRoomRepository.save(appRoom);
    }
}
