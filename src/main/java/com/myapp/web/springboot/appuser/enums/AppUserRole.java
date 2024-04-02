package com.myapp.web.springboot.appuser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 유저 롤
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum AppUserRole {
    USER("USER", "일반 사용자"),
    ADMIN("ADMIN", "관리자"),
    AI("AI", "AI"),
    SYSTEM("SYSTEM", "시스템");

    private final String key;
    private final String title;

    public static AppUserRole fromString(String text) {
        for(AppUserRole e : AppUserRole.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return AppUserRole.USER;
    }
}
