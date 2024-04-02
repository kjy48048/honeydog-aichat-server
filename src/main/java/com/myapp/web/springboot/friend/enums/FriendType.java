package com.myapp.web.springboot.friend.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * AI  친구타입(AI),
 * HUMAN 친구타입(사람)
 */
@Getter
@RequiredArgsConstructor
public enum FriendType {
    AI, HUMAN;

    public static FriendType fromString(String text) {
        for(FriendType e : FriendType.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
