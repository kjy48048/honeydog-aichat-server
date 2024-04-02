package com.myapp.web.springboot.assistant.domain;


import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.assistant.enums.ApiCallStage;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 *     설명: OpenAi Assistant 호출 기록용 도메인
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
public class OpenAiAssistantHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long openAiAssistantHistoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID", referencedColumnName = "userUuid")
    private AppUser appUser; // 호출한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_UUID", referencedColumnName = "roomUuid")
    private AppRoom appRoom; // 방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AI_USER_UUID", referencedColumnName = "userUuid")
    private AppUser aiUser; // 호출한당한 AI 유저
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApiCallStage apiCallStage;
    @Column
    private String model; // AI 모델
    @Column
    private String openAiKey; // 키
    @Column
    private String assistantId;
    @Column
    private String threadId;
    @Column
    private String messageId;
    @Column
    private String runsId;
    @Column
    private String status;
    @Column
    private String errorMessage;

    @Builder
    public OpenAiAssistantHistory(Long openAiAssistantHistoryId, AppUser appUser, AppRoom appRoom, AppUser aiUser, ApiCallStage apiCallStage, String model, String openAiKey, String assistantId, String threadId, String messageId, String runsId, String status, String errorMessage) {
        this.openAiAssistantHistoryId = openAiAssistantHistoryId;
        this.appUser = appUser;
        this.appRoom = appRoom;
        this.aiUser = aiUser;
        this.apiCallStage = apiCallStage;
        this.model = model;
        this.openAiKey = openAiKey;
        this.assistantId = assistantId;
        this.threadId = threadId;
        this.messageId = messageId;
        this.runsId = runsId;
        this.status = status;
        this.errorMessage = errorMessage;
    }
}
