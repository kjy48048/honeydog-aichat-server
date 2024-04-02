package com.myapp.web.springboot.appuserroom.enums;

import com.myapp.web.springboot.appuser.enums.AppUserRole;

public enum UserRoomRole {
    HOST, GUEST, SYSTEM, AI;

    public static UserRoomRole fromString(String text) {
        for(UserRoomRole e : UserRoomRole.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
