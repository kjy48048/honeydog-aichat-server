package com.myapp.web.springboot.approom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱 채팅방 상태 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 1. 31.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum AppRoomStatus {
    OPEN,
    CLOSED;
    public static AppRoomStatus fromString(String text) {
        for(AppRoomStatus e : AppRoomStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
