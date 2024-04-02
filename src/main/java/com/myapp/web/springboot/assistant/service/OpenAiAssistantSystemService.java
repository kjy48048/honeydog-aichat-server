package com.myapp.web.springboot.assistant.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.web.springboot.appchat.dto.AppChatSaveRequestDto;
import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.service.AppRoomService;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.assistant.domain.OpenAiAssistantHistory;
import com.myapp.web.springboot.assistant.domain.OpenAiThread;
import com.myapp.web.springboot.assistant.dto.*;
import com.myapp.web.springboot.assistant.dto.field.*;
import com.myapp.web.springboot.assistant.enums.ApiCallStage;
import com.myapp.web.springboot.assistant.enums.RunStatus;
import com.myapp.web.springboot.assistant.enums.ThreadStatus;
import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.common.utils.EncryptionUtil;
import com.myapp.web.springboot.functionchat.service.FunctionChatSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * <pre>
 *     설명: 오픈 AI 어시스턴트 로직 서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 2. 14.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAiAssistantSystemService {
    private final OpenAiAssistantApiService assistantApiService;
    private final OpenAiThreadService threadService;
    private final OpenAiAssistantHistoryService apiHistoryService;
    private final AppUserService appUserService;
    private final AppRoomService appRoomService;
    private final FunctionChatSystemService functionChatSystemService;
    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * OpenAiAssistant를 호출할 때 사용할 메서드
     * @param openAiToken openAiToken
     * @param requestDto 메세지 저장 요청 dto
     * @return 응답결과
     */
    public CompletableFuture<OpenAiAssistantResponseDto> processChat(AppChatSaveRequestDto requestDto, String openAiToken, String model) {
        log.info("=== processChat ===");
        return this.callOpenAiAssistant(openAiToken, requestDto)
                .thenCompose(aResult -> {
                    if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(aResult.getRspCode()))) {
                        log.info("=== processChat callOpenAiAssistant success ===");
                        log.info("=== responseDto: {} ===", aResult);
                        return this.getReturnMessageAndHandle(aResult, openAiToken , model);
                    } else {
                        // SUCCESS가 아닌 경우 바로 반환
                        log.info("=== processChat callOpenAiAssistant fail ===");
                        log.info("=== responseDto: {} ===", aResult);
                        return CompletableFuture.completedFuture(aResult);
                    }
                })
                .exceptionally(ex -> {
                    log.error("=== processChat callOpenAiAssistant error ===");
                    log.error("error: {}", ex.getMessage(), ex);
                    OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto();
                    responseDto.setRspCode(ResponseCode.ERROR.name());
                    responseDto.setRspMsg("죄송합니다. 다시한번 말씀해주세요.");
                    return responseDto;
                });
    }

    /**
     * Open Ai Assistant, 쓰레드 조회 혹은 생성
     * @param openAiToken OPEN AI TOKEN(커스텀 으로 만든 경우 제공받은 것 사용 없으면 기본)
     * @param requestDto 챗 저장 요청 DTO
     * @return OpenAIAssistant 응답 내용 저장 DTO
     */
    @Async
    public CompletableFuture<OpenAiAssistantResponseDto> callOpenAiAssistant(String openAiToken, AppChatSaveRequestDto requestDto) {
        log.info("Executing callOpenAiAssistant in {}", Thread.currentThread().getName());

        // 1. aiChatter로 assistant_id 찾기
        AppUser appUser;
        AppUser aiUser;
        AppRoom appRoom;

        try {
            log.info("=== Thread {}: callOpenAiAssistant step 1 ===", Thread.currentThread().getName());
            appUser = appUserService.findById(requestDto.getUserUuid());
            aiUser = appUserService.findById(requestDto.getAiUserUuid());
            appRoom = appRoomService.findById(requestDto.getRoomUuid());
        } catch (Exception e) {
            log.error("Thread {}: callOpenAiAssistant > user and room find error, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .apiCallStage(ApiCallStage.ListAssistantsAPI)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build());
            OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // 2. AssistantData 조회
        AssistantResponseData assistantResponseData;
        String assistantId;
        try {
            log.info("=== Thread {}: callOpenAiAssistant step 2 ===", Thread.currentThread().getName());
            assistantResponseData = assistantApiService.getAssistantData(aiUser.getNick(), openAiToken);
            assistantId = assistantResponseData.getId();
        } catch(Exception e) {
            log.error("Thread {}: callOpenAiAssistant > get assistantResponseData error, e: {}", Thread.currentThread().getName(), e.getMessage(), e);
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.ListAssistantsAPI)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build());
            OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // 3. room_uuid로 저장된 thread_id 여부 찾기 / 없으면 발급
        String threadId = "";
        try {
            log.info("=== Thread {}: callOpenAiAssistant step 3 ===", Thread.currentThread().getName());
            List<OpenAiThread> threadList = threadService.getList(requestDto.getRoomUuid());
            // DB에 조회한 threadList값이 여러개 일 경우 첫번째 값만 반환하고 나머지 DEAD로 처리...
            if(threadList != null && !threadList.isEmpty()) {
                threadId = threadList.get(0).getThreadId();

                threadList.stream()
                        .skip(1)
                        .forEach(thread -> {
                            // openApi에 삭제요청...
                            assistantApiService.deleteThread(openAiToken, thread.getThreadId());
                            // DB 업데이트...
                            thread.updateThreadStatus(ThreadStatus.DEAD);
                            threadService.save(thread);
                        });
            }
        } catch (Exception e) {
            log.error("Thread {}: callOpenAiAssistant > get thread data error, e: {}", Thread.currentThread().getName(), e.getMessage());
            // api 호출 기록 저장...
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RetrieveThread)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build());
            OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // 4. thread 없는 경우 신규 생성
        try {
            log.info("=== Thread {}: callOpenAiAssistant step 4 ===", Thread.currentThread().getName());
            // 조회된 threadId가 없을 경우 신규 요청
            if(!StringUtils.hasText(threadId)) {
                ThreadResponseDto threadResponseDto = assistantApiService.createThread(openAiToken);
                threadId = threadResponseDto.getId();

                // 성공일 경우 thread db 저장...
                if(ResponseCode.SUCCESS.equals(ResponseCode.fromString(threadResponseDto.getRspCode()))) {
                    threadService.save(
                            OpenAiThread.builder()
                                    .threadId(threadId)
                                    .appUser(appUser)
                                    .appRoom(appRoom)
                                    .threadStatus(ThreadStatus.LIVE)
                                    .aiUser(aiUser)
                                    .object(threadResponseDto.getObject())
                                    .createAt(threadResponseDto.getCreatedAt())
                                    .metadata(objectMapper.writeValueAsString(threadResponseDto.getMetadata()))
                                    .build()
                    );
                } else {
                    log.error("Thread {}: callOpenAiAssistant > thread create fail, e: {}", Thread.currentThread().getName(), threadResponseDto.getRspMsg());
                    OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                            OpenAiAssistantHistory.builder()
                                    .appUser(appUser)
                                    .appRoom(appRoom)
                                    .aiUser(aiUser)
                                    .apiCallStage(ApiCallStage.CreateThread)
                                    .openAiKey(encryptionUtil.encrypt(openAiToken))
                                    .threadId(threadId)
                                    .status(ResponseCode.FAIL.name())
                                    .errorMessage(threadResponseDto.getRspMsg())
                                    .build()
                    );
                    OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
                    return CompletableFuture.completedFuture(responseDto);
                }
            }
        } catch (Exception e) {
            log.error("Thread {}: callOpenAiAssistant > thread create fail, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.CreateThread)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .threadId(threadId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }


        // 5. 쓰레드 조회...
        try {
            log.info("=== Thread {}: callOpenAiAssistant step 5 ===", Thread.currentThread().getName());
            assistantApiService.retrieveThread(openAiToken, threadId);
        } catch (Exception e) {
            log.error("Thread {}: callOpenAiAssistant > thread retrieve fail, e: {}", Thread.currentThread().getName(), e.getMessage());
            // api 호출 기록 저장...
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RetrieveThread)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .threadId(threadId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );

            OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // api 호출 기록 저장...
        OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                OpenAiAssistantHistory.builder()
                        .appUser(appUser)
                        .appRoom(appRoom)
                        .aiUser(aiUser)
                        .assistantId(assistantId)
                        .apiCallStage(ApiCallStage.RetrieveThread)
                        .openAiKey(encryptionUtil.encrypt(openAiToken))
                        .threadId(threadId)
                        .status(ResponseCode.SUCCESS.name())
                        .build()
        );
        // 마지막 값 세팅
        OpenAiAssistantResponseDto responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
        responseDto.setRequestMessage(requestDto.getMessage());
        return CompletableFuture.completedFuture(responseDto);
    }

    /**
     * Open Ai Assistant에 메세지 전달 및 응답 메세지 결과 확인 후 조회
     * @param openAiToken OPEN AI TOKEN
     * @param responseDto 응답DTO
     * @return Open Ai Assistant 이력 dto
     */
    @Async
    public CompletableFuture<OpenAiAssistantResponseDto> getReturnMessageAndHandle(OpenAiAssistantResponseDto responseDto, String openAiToken, String model) {
        log.info("Executing getReturnMessageAndHandle in " + Thread.currentThread().getName());
        String assistantId = responseDto.getAssistantId();
        String threadId = responseDto.getThreadId();

        AppUser appUser;
        AppUser aiUser;
        AppRoom appRoom;
        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 1 ===", Thread.currentThread().getName());
            appUser = appUserService.findById(responseDto.getUserUuid());
            aiUser = appUserService.findById(responseDto.getAiUserUuid());
            appRoom = appRoomService.findById(responseDto.getRoomUuid());
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > user and room find error, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .apiCallStage(ApiCallStage.CreateMessage)
                            .threadId(threadId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // open Ai Assistant에 메세지 생성 요청
        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 2 ===", Thread.currentThread().getName());
            assistantApiService.createMessage(openAiToken, threadId,
                    MessageRequestDto.builder()
                            .role("user")
                            .content(responseDto.getRequestMessage())
                            .build()
            );
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > create message error, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.CreateMessage)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .threadId(threadId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // 5. runs 실행 runs_id 획득
        String runsId;
        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 3 ===", Thread.currentThread().getName());
            // 성공할 때 까지 최대 5번 실행...
            String openaiModelId = StringUtils.hasText(model) ? model : "gpt-3.5-turbo-0613";
            RunsResponseDto runsResponseDto = assistantApiService.createRun(openAiToken, openaiModelId, threadId,
                    RunsRequestDto.builder()
                            .assistantId(assistantId)
                            .build()
            );

            runsId = runsResponseDto.getId();
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > runs error, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.CreateRun)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .assistantId(assistantId)
                            .threadId(threadId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // 6. runs 조회 completed 되면
        int runsCount = 0;
        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 4 ===", Thread.currentThread().getName());
            boolean isSubmit = false;

            // 성공할 때 까지 최대 5번 실행...
            while(true) {
                RunsResponseDto runsSuccessResponseDto = assistantApiService.retrieveRun(openAiToken, threadId, runsId);

                if(RunStatus.REQUIRES_ACTION.getStatus().equals(runsSuccessResponseDto.getStatus()) && !isSubmit) {
                    log.info("Thread {}: getReturnMessageAndHandle > SubmitToolOutputsToRun Requires Action", Thread.currentThread().getName());

                    List<ToolCalls> requiredToolCalls = runsSuccessResponseDto.getRequiredAction().getSubmitToolOutputs().getToolCalls();
                    List<ToolOutput> toolOutputList = new ArrayList<>();
                    for (ToolCalls requiredToolCall : requiredToolCalls) {
                        String name = requiredToolCall.getFunction().getName();
                        String arguments = requiredToolCall.getFunction().getArguments();
                        ToolOutput toolOutput = functionChatSystemService.getToolOutput(name, arguments, requiredToolCall.getId());
                        toolOutputList.add(toolOutput);
                    }
                    ToolOutputs toolOutputs = new ToolOutputs();
                    toolOutputs.setToolOutputs(toolOutputList);
                    assistantApiService.submitToolOutputsToRun(openAiToken, threadId, runsId, toolOutputs);

                    // 건너띔
                    isSubmit = true;
                    continue;
                } else if(RunStatus.COMPLETED.getStatus().equals(runsSuccessResponseDto.getStatus())) {
                    log.info("Thread {}: getReturnMessageAndHandle > runs retrieve Completed", Thread.currentThread().getName());
                    break;
                } else {
                    log.info("Thread {}: getReturnMessageAndHandle > thread sleep call... status:{}, runsCount: {}"
                            , Thread.currentThread().getName(), RunStatus.fromStatus(runsSuccessResponseDto.getStatus()), runsCount);
                    Thread.sleep(5000); // 시뮬레이션을 위한 대기 시간
                }

                // 1분30초...
                if(runsCount >= 18) {
                    log.error("Thread {}: getReturnMessageAndHandle > runs retrieve timeout", Thread.currentThread().getName());
                    responseDto.setRspCode(ResponseCode.FAIL.name());
                    responseDto.setRspMsg("check time out...");
                    return CompletableFuture.completedFuture(responseDto);
                }
                runsCount++;
            }
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > runs retrieve, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RetrieveRun)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .assistantId(assistantId)
                            .threadId(threadId)
                            .runsId(runsId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        // MessageId 조회용...
        List<RunsResponseStepData> runsResponseStepDataList;
        RunsResponseStepDto runsResponseStepDto;
        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 5 ===", Thread.currentThread().getName());

            runsResponseStepDto = assistantApiService.getRunsListSteps(openAiToken, threadId, runsId);
            runsResponseStepDataList = runsResponseStepDto.getRunsResponseStepData();
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > runs list step error, threadId: {}, runsId: {}, e: {}"
                    , Thread.currentThread().getName(), threadId, runsId, e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RunsListStep)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .assistantId(assistantId)
                            .threadId(threadId)
                            .runsId(runsId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        List<String> responseMessageIdList;
        try {
            responseMessageIdList = runsResponseStepDataList
                    .stream()
                    .map(RunsResponseStepData::getStepDetails)
                    .filter(Objects::nonNull)
                    .map(StepDetails::getMessageCreation)
                    .filter(Objects::nonNull)
                    .map(MessageCreation::getMessageId)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > runs list step responseMessageIdList create error, threadId: {}, runsId: {}, e: {}"
                    , Thread.currentThread().getName(), threadId, runsId, e.getMessage(), e);
            try {
                log.error("Thread {}: getReturnMessageAndHandle > runsResponseStepDataList: {}"
                        , Thread.currentThread().getName(), objectMapper.writeValueAsString(runsResponseStepDataList));
            } catch (Exception e2) {
                log.error("Thread {}: getReturnMessageAndHandle > runsResponseStepDataList json parse error: {}...", Thread.currentThread().getName(), e2.getMessage(), e2);
            }
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RunsListStep)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .assistantId(assistantId)
                            .threadId(threadId)
                            .runsId(runsId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }


        //// stomp 응답 message 보내기
        StringBuilder returnMessage = new StringBuilder();
        String[] responseMessageIds = new String[responseMessageIdList.size()];
        int idx = 0;

        try {
            log.info("=== Thread {}: getReturnMessageAndHandle step 6 ===", Thread.currentThread().getName());
            for(String responseMessageId : responseMessageIdList) {
                MessageResponseDto responseMessageResponseDto = assistantApiService.retrieveMessage(openAiToken, threadId, responseMessageId);
                log.info("=== Thread {}: responseMessageResponseDto > {} ===", Thread.currentThread().getName(), responseMessageResponseDto);
                 // 응답메세지 담기...
                for(MessageContentData contentData : responseMessageResponseDto.getContent()) {
                    log.info("=== Thread {}: returnMessage > {} ===", Thread.currentThread().getName(), contentData.getText().getValue());
                    returnMessage.append(contentData.getText().getValue());
                }
                responseMessageIds[idx] = responseMessageId;
            }
        } catch (Exception e) {
            log.error("Thread {}: getReturnMessageAndHandle > retrieve message list for returnMessage, e: {}", Thread.currentThread().getName(), e.getMessage());
            OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                    OpenAiAssistantHistory.builder()
                            .appUser(appUser)
                            .appRoom(appRoom)
                            .aiUser(aiUser)
                            .apiCallStage(ApiCallStage.RetrieveMessage)
                            .openAiKey(encryptionUtil.encrypt(openAiToken))
                            .assistantId(assistantId)
                            .threadId(threadId)
                            .runsId(runsId)
                            .status(ResponseCode.ERROR.name())
                            .errorMessage(e.getMessage())
                            .build()
            );
            responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
            return CompletableFuture.completedFuture(responseDto);
        }

        OpenAiAssistantHistory openAiAssistantHistory = apiHistoryService.save(
                OpenAiAssistantHistory.builder()
                        .appUser(appUser)
                        .appRoom(appRoom)
                        .aiUser(aiUser)
                        .apiCallStage(ApiCallStage.RetrieveMessage)
                        .openAiKey(encryptionUtil.encrypt(openAiToken))
                        .assistantId(assistantId)
                        .threadId(threadId)
                        .runsId(runsId)
                        .messageId(Arrays.toString(responseMessageIds))
                        .status(ResponseCode.SUCCESS.name())
                        .build()
        );
        responseDto = new OpenAiAssistantResponseDto(openAiAssistantHistory);
        responseDto.setRspMsg(returnMessage.toString());
        return CompletableFuture.completedFuture(responseDto);
    }
}
