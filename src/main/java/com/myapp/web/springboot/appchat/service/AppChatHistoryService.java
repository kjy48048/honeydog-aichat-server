package com.myapp.web.springboot.appchat.service;


import com.myapp.web.springboot.appchat.domain.AppChatHistory;
import com.myapp.web.springboot.appchat.dto.AppChatResponseDto;
import com.myapp.web.springboot.appchat.dto.AppChatSaveRequestDto;
import com.myapp.web.springboot.appchat.enums.AppMessageType;
import com.myapp.web.springboot.appchat.repository.AppChatHistoryRepository;
import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.repository.AppRoomRepository;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  <pre>
 *      설명: 앱용 유저 채팅방 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 23.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppChatHistoryService {
    private final AppChatHistoryRepository appChatHistoryRepository;
    private final AppUserRepository appUserRepository;
    private final AppRoomRepository appRoomRepository;

    /**
     * 앱용 채팅 기록 저장 요청
     * @param entity 앱용 채팅 저장 기록 요청 엔티티
     * @return 채팅 ID
     */
    @Transactional
    public AppChatHistory save(AppChatHistory entity) {
        return appChatHistoryRepository.save(entity);
    }

    /**
     * 앱용 채팅 기록 저장 요청
     * @param requestDto 앱용 채팅 저장 기록 요청 Dto
     * @param messageType 메세지 타입
     * @return 채팅 ID
     */
    @Transactional
    public AppChatHistory save(AppChatSaveRequestDto requestDto, String message, AppMessageType messageType) {

        log.info("AppChatHistory save requestDto: {}, message: {}, messageType: {}", requestDto, message, messageType);

        String userUuid = requestDto.getUserUuid();
        String roomUuid = requestDto.getRoomUuid();

        AppUser appUser = appUserRepository.findByUserUuid(UUID.fromString(userUuid));
        if(appUser != null) {
            log.info("appUser findByUserUuid: {}, findUserUuid: {}", appUser, userUuid);
        } else {
            log.error("AppChatHistory save error... appUser not found");
            throw new IllegalArgumentException();
        }

        AppRoom appRoom = appRoomRepository.findAppRoomByRoomUuidAndRoomStatus(UUID.fromString(roomUuid), AppRoomStatus.OPEN);
        if(appRoom != null) {
            log.info("appRoom findAppRoomByRoomUuidAndRoomStatus appRoom: {}, roomUuid: {}", appRoom, roomUuid);
        } else {
            log.error("AppChatHistory save error... appRoom not found");
            throw new IllegalArgumentException();
        }

        AppChatHistory entity = AppChatHistory.builder()
                .appUser(appUser)
                .appRoom(appRoom)
                .messageType(messageType)
                .message(message)
                .build();

        log.info("appChatHistory save entity: {}", entity);

        return this.save(entity);
    }

    /**
     * 앱용 채팅 기록 저장 요청
     * @param requestDto 앱용 채팅 저장 기록 요청 Dto
     * @param messageType 메세지 타입
     * @return 채팅 ID
     */
    @Transactional
    public AppChatHistory save(AppChatSaveRequestDto requestDto, AppMessageType messageType) {
        String message = requestDto.getMessage();
        return this.save(requestDto, message, messageType);
    }

    /**
     * 앱용 채팅 기록 저장 요청
     * @param requestDto 앱용 채팅 저장 기록 요청 Dto
     * @return 채팅 ID
     */
    @Transactional
    public AppChatHistory save(AppChatSaveRequestDto requestDto) {
        AppMessageType messageType = AppMessageType.fromString(requestDto.getMessageType());
        return this.save(requestDto, messageType != null ? messageType : AppMessageType.TALK);
    }


    /**
     * 앱용 채팅 기록 조회 요청
     * @param roomUuid 채팅방 uuid
     * @return 채팅방 채팅 기록
     */
    public List<AppChatResponseDto> findChatListByRoomUuid(String roomUuid, Pageable pageable) {
        return appChatHistoryRepository.findAllChatByRoomUuidDesc(UUID.fromString(roomUuid), pageable)
                .stream().map(AppChatResponseDto::new).collect(Collectors.toList());
    }

    /**
     * 앱용 입장 기록 체크
     * @param roomUuid 채팅방 uuid
     * @return 채팅방 채팅 기록
     */
    public boolean checkEnterByRoomUuid(String roomUuid) {
        List<AppChatHistory> enterChat = appChatHistoryRepository.findEnterChatByRoomUuid(UUID.fromString(roomUuid), AppMessageType.ENTER);
        return enterChat != null && enterChat.size() > 0;
    }

    /**
     * 채팅방 목록에 보여줄 마지막 채팅내용
     * @param roomUuid 채팅방 id
     * @return 마지막채팅
     */
    public AppChatHistory findLastChatByRoomUuid(String roomUuid) {
        List<AppChatHistory> allChat = appChatHistoryRepository.findAllChatNoneEnterByRoomUuid(UUID.fromString(roomUuid));
        return allChat != null && allChat.size() > 0 ? allChat.get(0) : null;
    }
}
