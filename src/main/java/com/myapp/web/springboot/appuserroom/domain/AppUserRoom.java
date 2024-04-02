package com.myapp.web.springboot.appuserroom.domain;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * <pre>
 *     설명: 앱용 채팅방-유저 관계 도메인, 다대다(N:M) 관계용 엔티티
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AppUserRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long appUserRoomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID", referencedColumnName = "userUuid")
    private AppUser appUser; // 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOM_UUID", referencedColumnName = "roomUuid")
    private AppRoom appRoom; // 방

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoomRole userRoomRole; // 호스트, 게스트, 시스템
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRoomStatus userRoomStatus; // In, Out
    @Column
    private LocalDateTime inDateTime;
    @Column
    private LocalDateTime outDateTime;

    @Builder
    public AppUserRoom(Long appUserRoomId, AppUser appUser, AppRoom appRoom, UserRoomRole userRoomRole, UserRoomStatus userRoomStatus, LocalDateTime inDateTime, LocalDateTime outDateTime) {
        this.appUserRoomId = appUserRoomId;
        this.appUser = appUser;
        this.appRoom = appRoom;
        this.userRoomRole = userRoomRole;
        this.userRoomStatus = userRoomStatus;
        this.inDateTime = inDateTime;
        this.outDateTime = outDateTime;
    }

    public AppUserRoom inRoom() {
        this.userRoomStatus = UserRoomStatus.IN;
        this.inDateTime = LocalDateTime.now();
        return this;
    }

    public AppUserRoom outRoom() {
        this.userRoomStatus = UserRoomStatus.OUT;
        this.outDateTime = LocalDateTime.now();
        return this;
    }
}
