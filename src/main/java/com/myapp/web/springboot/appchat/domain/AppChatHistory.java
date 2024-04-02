package com.myapp.web.springboot.appchat.domain;

import com.myapp.web.springboot.appchat.enums.AppMessageType;
import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 채팅 기록 도메인
 *     작성자: 김진영
 *     작성일: 2023. 10. 20.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AppChatHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appChatHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID")
    private AppUser appUser; // 채팅방 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_UUID")
    private AppRoom appRoom; // 채팅방

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppMessageType messageType; // 메세지 타입

    @Column(length = 4000, nullable = false)
    private String message; // 메세지

    @Builder
    public AppChatHistory(Long appChatHistoryId, AppUser appUser, AppRoom appRoom, AppMessageType messageType, String message) {
        this.appChatHistoryId = appChatHistoryId;
        this.appUser = appUser;
        this.appRoom = appRoom;
        this.messageType = messageType;
        this.message = message;
    }
}
