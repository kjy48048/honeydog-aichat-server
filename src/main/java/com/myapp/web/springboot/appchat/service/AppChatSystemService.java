package com.myapp.web.springboot.appchat.service;

import com.myapp.web.springboot.aichatter.dto.AiChatterApiRequestDto;
import com.myapp.web.springboot.aichatter.service.AiChatterService;
import com.myapp.web.springboot.appchat.domain.AppChatHistory;
import com.myapp.web.springboot.appchat.dto.AppChatCallResponseDto;
import com.myapp.web.springboot.appchat.dto.AppChatResponseDto;
import com.myapp.web.springboot.appchat.dto.AppChatSaveRequestDto;
import com.myapp.web.springboot.appchat.enums.AppMessageType;
import com.myapp.web.springboot.approom.dto.AppRoomResponseDto;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.approom.service.AppRoomService;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.assistant.dto.AssistantResponseData;
import com.myapp.web.springboot.assistant.dto.OpenAiAssistantResponseDto;
import com.myapp.web.springboot.assistant.service.OpenAiAssistantApiService;
import com.myapp.web.springboot.assistant.service.OpenAiAssistantSystemService;
import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.common.utils.EncryptionUtil;
import com.myapp.web.springboot.openchat.service.OpenChatApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <pre>
 *     설명: 앱용 채팅 시스템 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppChatSystemService {
    private final AppChatHistoryService appChatHistoryService;
    private final AppUserService appUserService;
    private final AppRoomService appRoomService;
    private final AiChatterService aiChatterService;
    private final OpenChatApiService openChatApiService;
    private final OpenAiAssistantSystemService openAiAssistantSystemService;
    private final OpenAiAssistantApiService openAiAssistantApiService;
    private final EncryptionUtil encryptionUtil;
    private final SimpMessageSendingOperations sendingOperations;
    private AppUserResponseDto systemUser;

    /**
     * 메세지 처리 로직 V2
     * - 앱 개발용으로 새로 개발중.(23.11.08)
     * @param requestDto 요청DTO
     */
    @Async
    public CompletableFuture<AppChatCallResponseDto> messageHandleV2(AppChatSaveRequestDto requestDto) {
        AppChatCallResponseDto callResponseDto = new AppChatCallResponseDto();
        try {
            String returnMessage = this.handleMessage(requestDto);
            callResponseDto.setReturnMessage(returnMessage);
            callResponseDto.setRspMsg("Success");
            callResponseDto.setRspCode("200");
        } catch (Exception e) {
            log.error("messageHandleV2 error...", e);
            callResponseDto.setRspMsg(e.getLocalizedMessage());
            callResponseDto.setRspCode("999");
        }
        return CompletableFuture.completedFuture(callResponseDto);
    }

    /**
     * 메세지 처리 로직
     * @param requestDto 요청DTO
     * @return 응답받은 메세지
     */
    private String handleMessage(AppChatSaveRequestDto requestDto) {
        AppMessageType type = AppMessageType.fromString(requestDto.getMessageType());
        String nick = requestDto.getNick() == null ? "게스트" : requestDto.getNick();
        String returnMessage = "";
        AppChatHistory returnAppChatHistory;

        // 유저 질문 채팅 기록...
        if(type != AppMessageType.ENTER) {
            returnAppChatHistory = appChatHistoryService.save(requestDto, AppMessageType.TALK);
            {
                AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                this.sendingOperation(requestDto, responseDto);
            }
        }
        switch (type) {
            case SYSTEM -> {
                // 시스템 요청 메세지...
                log.info("SYSTEM requestDto: {}", requestDto);
                // 입장 메세지 없으면 처리
                AppUserResponseDto systemChatter = this.getSystemUser();
                returnMessage = requestDto.getMessage();

                AppChatSaveRequestDto systemMessageRequestDto = AppChatSaveRequestDto.builder()
                        .userUuid(systemChatter.getUserUuid())
                        .nick(systemChatter.getNick())
                        .roomUuid(requestDto.getRoomUuid())
                        .message(returnMessage)
                        .build();

                // 시스템 환영 채팅 기록...
                returnAppChatHistory = appChatHistoryService.save(systemMessageRequestDto, returnMessage, AppMessageType.SYSTEM);
                AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                this.sendingOperation(requestDto, responseDto);
            }
            case AI_TALK -> {
                // 챗GPT 요청 메세지...
                log.info("CHATGPT requestDto: {}", requestDto);
                // 챗GPT 처리...
                AppUserResponseDto aiUser = appUserService.findByUserUuid(requestDto.getAiUserUuid());

                AppChatSaveRequestDto aiUserRequestDto = AppChatSaveRequestDto.builder()
                        .userUuid(aiUser.getUserUuid())
                        .nick(aiUser.getNick())
                        .roomUuid(requestDto.getRoomUuid())
                        .message(requestDto.getMessage())
                        .build();

                AiChatterApiRequestDto aiChatter = aiChatterService.findForApiByAiUser(aiUser.getUserUuid());

                // OpenAi Assistant에 존재하는지 확인
                AssistantResponseData assistantResponseData = openAiAssistantApiService.getAssistantData(aiChatter.getNick(), encryptionUtil.decrypt(aiChatter.getEncryptToken()));
                if(assistantResponseData == null) {
                    // 없으면 일반 CHATGPT 답변 채팅 기록...
                    returnMessage = openChatApiService.sendMessage(aiUserRequestDto.getMessage(), nick);
                    returnAppChatHistory = appChatHistoryService.save(aiUserRequestDto, returnMessage, AppMessageType.AI_TALK);
                    {
                        AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                        this.sendingOperation(requestDto, responseDto);
                    }
                } else {
                    CompletableFuture<OpenAiAssistantResponseDto> future = openAiAssistantSystemService.processChat(requestDto
                            , encryptionUtil.decrypt(aiChatter.getEncryptToken()), aiChatter.getModel());
                    try {
                        OpenAiAssistantResponseDto openAiAssistantResponseDto = future.get();
                        returnMessage = openAiAssistantResponseDto.getRspMsg();
                        returnMessage = StringUtils.hasText(returnMessage) ? returnMessage : "죄송합니다. 다시 한번 말씀해주세요.";
                        returnAppChatHistory = appChatHistoryService.save(aiUserRequestDto, returnMessage, AppMessageType.AI_TALK);
                        if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(openAiAssistantResponseDto.getRspCode()))) {
                            AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                            this.sendingOperation(requestDto, responseDto);
                        } else {
                            log.info("openAiAssistantSystemService fail... openAiAssistantResponseDto: {}", openAiAssistantResponseDto);
                            AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                            responseDto.setMessage("죄송합니다. 다시 한번 말씀해주세요.");
                            this.sendingOperation(requestDto, responseDto);
                        }
                    } catch (Exception e) {
                        log.error("openAiAssistantSystemService error... {}", e.getMessage(), e);
                    }

                }
            }
            case ENTER -> {
                // 입장 요청 메세지...
                log.info("ENTER requestDto: {}", requestDto);
                // 입장 시스템 메세지 처리...

                // 입장한지 여부 체크
                boolean hasEnterChat = appChatHistoryService.checkEnterByRoomUuid(requestDto.getRoomUuid());

                if(!hasEnterChat) {
                    // 입장 메세지 없으면 처리
                    AppUserResponseDto systemChatter = this.getSystemUser();
                    returnMessage = requestDto.getNick() + "님이 입장하셨습니다.";

                    AppChatSaveRequestDto systemMessageRequestDto = AppChatSaveRequestDto.builder()
                            .userUuid(systemChatter.getUserUuid())
                            .nick(systemChatter.getNick())
                            .roomUuid(requestDto.getRoomUuid())
                            .message(returnMessage)
                            .build();

                    // 시스템 환영 채팅 기록...
                    returnAppChatHistory = appChatHistoryService.save(systemMessageRequestDto, returnMessage, AppMessageType.ENTER);
                    {
                        AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                        this.sendingOperation(requestDto, responseDto);
                    }

                    // AI 방인지 체크 AI 방이면 자기소개 요청!
                    AppRoomResponseDto roomResponseDto = appRoomService.findRoom(requestDto.getRoomUuid());
                    if(AppRoomType.AI.equals(roomResponseDto.getRoomType())) {
                        AppUser aiUser = appUserService.findAiUserByRoomUuid(UUID.fromString(requestDto.getRoomUuid()));
                        AiChatterApiRequestDto aiChatter = aiChatterService.findForApiByAiUser(aiUser.getUserUuid().toString());

                        // OpenAi Assistant에 존재하는지 확인
                        AssistantResponseData assistantResponseData = openAiAssistantApiService.getAssistantData(aiChatter.getNick(), encryptionUtil.decrypt(aiChatter.getEncryptToken()));
                        if(assistantResponseData != null) {
                            AppChatSaveRequestDto aiUserGreetingRequestDto = AppChatSaveRequestDto.builder()
                                    .userUuid(aiUser.getUserUuid().toString())
                                    .aiUserUuid(aiUser.getUserUuid().toString())
                                    .nick(aiUser.getNick())
                                    .roomUuid(requestDto.getRoomUuid())
                                    .message(aiUser.getGreetings())
                                    .build();

                            returnAppChatHistory = appChatHistoryService.save(aiUserGreetingRequestDto, aiUser.getGreetings(), AppMessageType.AI_TALK);
                            AppChatResponseDto responseDto = new AppChatResponseDto(returnAppChatHistory);
                            this.sendingOperation(aiUserGreetingRequestDto, responseDto);
                        }
                    }
                }
            }

            case TALK ->
                // 일반적인 메세지... 응답 처리 X...
                log.info("TALK requestDto: {}", requestDto);
            default ->
                // 이쪽으로 오면 안됨...
                log.info("Unchecked type... requestDto: {}", requestDto);
        }

        return returnMessage;
    }

    /**
     * 메세지를 목적지에 보냄
     * @param requestDto 요청DTO
     * @param responseDto 응답DTO
     */
    private void sendingOperation(AppChatSaveRequestDto requestDto, AppChatResponseDto responseDto) {
        sendingOperations.convertAndSend("/topic/chat/room/"+requestDto.getRoomUuid(), responseDto);
    }


    /**
     * 시스템 유저 요청
     * @return 시스템 유저정보 Dto
     */
    private AppUserResponseDto getSystemUser() {
        if(this.systemUser != null) {
            return this.systemUser;
        }
        AppUserResponseDto appUserResponseDto;
        appUserResponseDto = appUserService.findOrSaveSystemUser();
        // 필드 변수에 값 생성
        this.systemUser = appUserResponseDto;
        return appUserResponseDto;
    }
}
