package com.myapp.web.springboot.approom.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱 채팅방 종류 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 1. 31.
 *     AI: AI와 1:1대화방
 *     FRIEND: 친구와 1:1대화방(친구와 대화하기 누르면 제일 마지막으로 1:1 대화한 방 들어가도록...)
 *     FRIEND_GROUP: 친구들 모임
 *     ALL: AI와 친구 둘다 있는 경우... AI와 대화하기 모드, 친구와 대화하기 모드 구분
 * </pre>
 */

@Getter
@RequiredArgsConstructor
public enum AppRoomType {
    AI, FRIEND, FRIEND_GROUP, ALL;

    public static AppRoomType fromString(String text) {
        for(AppRoomType e : AppRoomType.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
