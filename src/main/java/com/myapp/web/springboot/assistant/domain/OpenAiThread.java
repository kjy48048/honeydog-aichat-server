package com.myapp.web.springboot.assistant.domain;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.assistant.enums.ThreadStatus;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: OpenAi Assistant Thread 제어용 도메인
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class OpenAiThread extends BaseEntity {
    @Id
    private String threadId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID", referencedColumnName = "userUuid")
    private AppUser appUser; // 생성한 유저
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_UUID", referencedColumnName = "roomUuid")
    private AppRoom appRoom; // 방
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AI_USER_UUID", referencedColumnName = "userUuid")
    private AppUser aiUser; // 호출한당한 AI 유저
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ThreadStatus threadStatus;
    @Column
    private String object;
    @Column
    private Long createAt;
    @Column
    private String metadata;

    @Builder
    public OpenAiThread(String threadId, AppUser appUser, AppRoom appRoom, ThreadStatus threadStatus, AppUser aiUser,
                        String object, Long createAt, String metadata) {
        this.threadId = threadId;
        this.appUser = appUser;
        this.appRoom = appRoom;
        this.threadStatus = threadStatus;
        this.aiUser = aiUser;
        this.object = object;
        this.createAt = createAt;
        this.metadata = metadata;
    }

    public OpenAiThread updateThreadStatus(ThreadStatus threadStatus) {
        this.threadStatus = threadStatus;
        return this;
    }
}
