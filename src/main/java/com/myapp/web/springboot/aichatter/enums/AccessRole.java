package com.myapp.web.springboot.aichatter.enums;

/**
 * <pre>
 *     설명: Open Ai Assistants 접근 제한관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 *
 *     PUBLIC: 제한 없음
 *     PRIVATE: 나만
 *     FRIEND: 친구만
 * </pre>
 */
public enum AccessRole {
    PUBLIC, PRIVATE, FRIEND;

    public static AccessRole fromString(String text) {
        for(AccessRole e : AccessRole.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
