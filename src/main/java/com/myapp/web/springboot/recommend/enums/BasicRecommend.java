package com.myapp.web.springboot.recommend.enums;

import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 기본 추천질문
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 17.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum BasicRecommend {
    INTRODUCTION("", "", "", 1L),
    TAROT("", "", "", 2L),
    MBTI("", "", "", 3L),
    BEAST_MASTER("", "", "", 4L);

    private final String nick;
    private final String question;
    private final String color;
    private final Long orderIndex;

    public static AppUserStatus fromString(String text) {
        for(AppUserStatus e : AppUserStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return AppUserStatus.ABNORMAL;
    }
}
