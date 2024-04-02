package com.myapp.web.springboot.appuserroom.enums;

public enum UserRoomStatus {
    IN, OUT;

    public static UserRoomStatus fromString(String text) {
        for(UserRoomStatus e : UserRoomStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
