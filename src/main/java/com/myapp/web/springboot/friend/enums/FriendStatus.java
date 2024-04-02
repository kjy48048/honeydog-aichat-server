package com.myapp.web.springboot.friend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * REQUESTED 요청한 친구,
 * ACCEPTED 수락된 친구,
 * BLOCKED 차단한 친구,
 * NO_RELATION 무관계(요청하거나 차단했다가 취소한 상태) - 추천친구로 활용?
 */
@Getter
@RequiredArgsConstructor
public enum FriendStatus {
    REQUESTED, ACCEPTED, BLOCKED, NO_RELATION;

    public static FriendStatus fromString(String text) {
        for(FriendStatus e : FriendStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
