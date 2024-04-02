package com.myapp.web.springboot.appchat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 앱용 채팅방 메세지 타입
 *     메세지/AI채팅/시스템/타로...
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 08.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum AppMessageType {
    ENTER("입장"),
    TALK("메세지"),
    AI_TALK("AI채팅"),
    SYSTEM("시스템"),
    AI_CONSULTING("AI상담");

    private final String description;

    public static AppMessageType fromString(String text) {
        for(AppMessageType e : AppMessageType.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        
        // 예외의 경우 TALK로 기본세팅
        return TALK;
    }
}
