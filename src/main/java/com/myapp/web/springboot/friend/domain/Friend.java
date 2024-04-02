package com.myapp.web.springboot.friend.domain;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.domain.BaseEntity;
import com.myapp.web.springboot.friend.enums.FriendStatus;
import com.myapp.web.springboot.friend.enums.FriendType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 친구 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 01. 31.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class Friend extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long friendId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_UUID")
    private AppUser user; // 채팅방 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FRIEND_USER_UUID")
    private AppUser friendUser; // 채팅방 유저

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendStatus friendStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FriendType friendType;

    @Builder
    public Friend(Long friendId, AppUser user, AppUser friendUser, FriendStatus friendStatus, FriendType friendType) {
        this.friendId = friendId;
        this.user = user;
        this.friendUser = friendUser;
        this.friendStatus = friendStatus;
        this.friendType = friendType;
    }

    public Friend updateStatus(FriendStatus friendStatus) {
        this.friendStatus = friendStatus;
        return this;
    }
}
