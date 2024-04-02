package com.myapp.web.springboot.aichatter.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 AI 채팅 상대
 *     HONEY_DOG(허니도그): 앱설명
 *     STELLA(스텔라): 강아지, 애완동물 관련
 *     MOMO(모모): MBTI
 *     LAYLA(라일라): 타로
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum BasicAiChatter {
    HONEY_DOG("", "", AccessRole.PUBLIC),
    STELLA("", "", "", AccessRole.PUBLIC),
    MOMO("", "", "", AccessRole.PUBLIC),
    LAYLA("", "", "", AccessRole.PUBLIC);

    private final String nick;
    private final String greetings;
    private final String picture;
    private final AccessRole accessRole;
    public static BasicAiChatter fromString(String text) {
        for(BasicAiChatter e : BasicAiChatter.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
