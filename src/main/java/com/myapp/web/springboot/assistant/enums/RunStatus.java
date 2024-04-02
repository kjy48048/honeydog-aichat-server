package com.myapp.web.springboot.assistant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 오픈AI 메세지 실행 상태관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum RunStatus {
    QUEUED("queued"),
    IN_PROGRESS("in_progress"),
    REQUIRES_ACTION("requires_action"),
    CANCELLING("cancelling"),
    CANCELLED("cancelled"),
    FAILED("failed"),
    COMPLETED("completed"),
    EXPIRED("expired")
    ;

    private final String status;

    public static RunStatus fromString(String text) {
        for(RunStatus e : RunStatus.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }

    public static RunStatus fromStatus(String text) {
        for(RunStatus e : RunStatus.values()) {
            if(e.getStatus().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
