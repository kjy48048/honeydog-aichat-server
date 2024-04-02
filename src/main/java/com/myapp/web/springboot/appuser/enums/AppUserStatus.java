package com.myapp.web.springboot.appuser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 유저 상태
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 06.
 *     NORMAL(일반), WITHDRAW(탈퇴), BANNED(정지), ABNORMAL(비정상)
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum AppUserStatus {
    NORMAL, WITHDRAW, BANNED, ABNORMAL;

    public static AppUserStatus fromString(String text) {
        for(AppUserStatus e : AppUserStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return AppUserStatus.ABNORMAL;
    }
}
