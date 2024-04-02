package com.myapp.web.springboot.approom.domain;

import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.approom.enums.AppRoomType;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * <pre>
 *      설명: 앱용 채팅방 엔티티
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 23.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AppRoom extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID roomUuid;

    @Column
    private String roomNick;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRoomStatus roomStatus; // 열림/닫힘

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppRoomType roomType; // AI / FRIEND / FRIEND_GROUP / ALL

    @Builder
    public AppRoom(UUID roomUuid, String roomNick, AppRoomStatus roomStatus, AppRoomType roomType) {
        this.roomUuid = roomUuid;
        this.roomNick = roomNick;
        this.roomStatus = roomStatus;
        this.roomType = roomType;
    }

    public AppRoom updateRoomNick(String roomNick) {
        this.roomNick = roomNick;
        return this;
    }
    public AppRoom updateRoomStatus(AppRoomStatus roomStatus) {
        this.roomStatus = roomStatus;
        return this;
    }

    public AppRoom updateRoomType(AppRoomType roomType) {
        this.roomType = roomType;
        return this;
    }
}
