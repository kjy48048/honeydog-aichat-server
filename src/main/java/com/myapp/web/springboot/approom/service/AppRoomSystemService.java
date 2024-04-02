package com.myapp.web.springboot.approom.service;

import com.myapp.web.springboot.appchat.domain.AppChatHistory;
import com.myapp.web.springboot.appchat.service.AppChatHistoryService;
import com.myapp.web.springboot.approom.dto.AppRoomListResponseDto;
import com.myapp.web.springboot.approom.dto.AppRoomRequestDto;
import com.myapp.web.springboot.approom.dto.AppRoomResponseDto;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 *     설명: 앱용 채팅방 로직처리 서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 16.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppRoomSystemService {
    private final AppRoomService appRoomService;
    private final AppUserRoomService appUserRoomService;
    private final AppUserService appUserService;
    private final AppChatHistoryService appChatHistoryService;

    public AppRoomResponseDto enterRoom(AppRoomRequestDto requestDto) {
        try {
            AppRoomType requestAppRoomType = AppRoomType.fromString(requestDto.getRoomType());

            if(requestAppRoomType != null
                    && (requestAppRoomType.equals(AppRoomType.AI) || requestAppRoomType.equals(AppRoomType.FRIEND))) {
                // 1:1 채팅방일 경우 최근 대화방 가져오기...
                log.info("=== findRecentRoom ===");
                AppRoomResponseDto responseDto = appRoomService.findRecentRoom(requestDto);
                if(responseDto != null) {
                    return responseDto;
                }
            }
            log.info("=== createRoom ===");
            return appRoomService.createRoom(requestDto);
        } catch (Exception e) {
            log.error("app room save error... requestDto: {}", requestDto, e);
            return null;
        }
    }

    @Transactional
    public AppRoomResponseDto outRoom(String roomUuid) {
        long notAiUserCount;
        try {
            List<AppUserRoomResponseDto> joinUserList = appUserRoomService.findJoinUsersInRoom(roomUuid);
            if(joinUserList != null) {
                log.info("outRoom room out process start... roomUuid:{}", roomUuid);
                for(AppUserRoomResponseDto joinUser : joinUserList) {
                    // 채팅방 나가기 처리...
                    if(joinUser.getAppRoomUuid().equals(roomUuid)) {
                        appUserRoomService.updateUserRoomStatus(joinUser.getAppUserUuid(), roomUuid, UserRoomStatus.OUT);
                    }
                }
                // 시스템이 아니고 나간 유저 제외한 유저수 카운트...
                notAiUserCount = joinUserList
                        .stream()
                        .filter(joinUser -> !joinUser.getUserRoomRole().equals(UserRoomRole.SYSTEM))
                        .filter(joinUser -> !joinUser.getAppRoomUuid().equals(roomUuid))
                        .count();
                log.info("outRoom room out process stop... roomUuid:{}, joinUserList'size:{}, notAiUserCount: {}", joinUserList.size(), roomUuid, notAiUserCount);
            } else {
                log.error("outRoom joinUserList is null roomUuid: {}", roomUuid);
                throw new IllegalArgumentException();
            }

            return notAiUserCount == 0
                    ? appRoomService.closeRoom(roomUuid)
                    : appRoomService.findRoom(roomUuid);
        } catch (Exception e) {
            log.error("room save error... roomUuid: {}", roomUuid, e);
            return null;
        }
    }

    public List<AppRoomListResponseDto> findJoinRooms(String userUuid) {
        try {
            List<AppRoomListResponseDto> responseDtoList = appRoomService.findJoinRooms(userUuid);

            // 채팅방 대표이미지 찾기 && ai 채터 설정
            if(responseDtoList != null && responseDtoList.size() > 0) {
                for(AppRoomListResponseDto responseDto : responseDtoList) {
                    String roomUuid = responseDto.getRoomUuid();

                    List<AppUserRoomResponseDto> appUsersInRoom = appUserRoomService.findJoinUsersInRoom(roomUuid);

                    // 제일 먼저 조회되는 것 찾기...
                    List<AppUserRoomResponseDto> anyOtherUsersInRoom = appUsersInRoom
                            .stream()
                            .filter(e -> !e.getAppUserUuid().equals(userUuid))
                            .toList();

                    if(anyOtherUsersInRoom.size() > 0) {
                        // 대표 프로필 이미지 세팅
                        String anyOtherUserUuid = anyOtherUsersInRoom.get(0).getAppUserUuid();
                        AppUserResponseDto anyOtherUser = appUserService.findByUserUuid(anyOtherUserUuid);

                        if(anyOtherUser != null) {
                            responseDto.setPicture(anyOtherUser.getPicture());
                            // AI 유저 세팅
                            if(AppUserRole.AI.equals(AppUserRole.fromString(anyOtherUser.getAppUserRole()))) {
                                responseDto.setAiUserUuid(anyOtherUserUuid);
                            }

                            // 채팅방 닉네임(1:1채팅인경우 상대방으로)....
                            if(AppRoomType.AI.equals(responseDto.getRoomType())
                                    || AppRoomType.FRIEND.equals(responseDto.getRoomType())) {
                                responseDto.setRoomNick(anyOtherUser.getNick());
                            }
                        }

                        // 마지막 채팅 내용찾기
                        AppChatHistory appChat = appChatHistoryService.findLastChatByRoomUuid(roomUuid);
                        if(appChat != null) {
                            responseDto.setLastMessage(appChat.getMessage());
                        }
                    }
                }
                return responseDtoList;
            } else {
                log.info("findJoinRooms find fail...");
                return null;
            }
        } catch (Exception e) {
            log.error("findJoinRooms error..., e: {}", e.getMessage(), e);
            return null;
        }
    }
}
