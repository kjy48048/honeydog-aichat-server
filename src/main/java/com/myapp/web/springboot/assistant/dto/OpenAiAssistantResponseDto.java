package com.myapp.web.springboot.assistant.dto;

import com.myapp.web.springboot.assistant.domain.OpenAiAssistantHistory;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 API응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 15.
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class OpenAiAssistantResponseDto extends CommonResponseDto {
    private Long openAiAssistantHistoryId;
    private String userUuid; // 호출한 유저
    private String roomUuid; // 방
    private String aiUserUuid; // ai 유저
    private String model; // AI 모델
    private String openAiKey; // 키
    private String assistantId;
    private String threadId;
    private String messageId;
    private String runsId;
    private String status;
    private String errorMessage;

    // todo: 응답 내용에 적어야할 것들...
    private String processLevel;
    private String requestMessage;

    public OpenAiAssistantResponseDto(OpenAiAssistantHistory aiAssistantHistory) {
        this.openAiAssistantHistoryId = aiAssistantHistory.getOpenAiAssistantHistoryId();
        if(aiAssistantHistory.getAppUser() != null
        && aiAssistantHistory.getAppUser().getUserUuid() != null) {
            this.userUuid = aiAssistantHistory.getAppUser().getUserUuid().toString();
        }
        if(aiAssistantHistory.getAppRoom() != null
                && aiAssistantHistory.getAppRoom().getRoomUuid() != null) {
            this.roomUuid = aiAssistantHistory.getAppRoom().getRoomUuid().toString();
        }
        if(aiAssistantHistory.getAiUser() != null
            && aiAssistantHistory.getAiUser().getUserUuid() != null) {
            this.aiUserUuid = aiAssistantHistory.getAiUser().getUserUuid().toString();
        }
        this.model = aiAssistantHistory.getModel();
        this.openAiKey = aiAssistantHistory.getOpenAiKey();
        this.assistantId = aiAssistantHistory.getAssistantId();
        this.threadId = aiAssistantHistory.getThreadId();
        this.messageId = aiAssistantHistory.getMessageId();
        this.runsId = aiAssistantHistory.getRunsId();
        this.status = aiAssistantHistory.getStatus();
        this.errorMessage = aiAssistantHistory.getErrorMessage();

        this.setRspCode(aiAssistantHistory.getStatus());
        this.setRspMsg(aiAssistantHistory.getErrorMessage());
    }
}
