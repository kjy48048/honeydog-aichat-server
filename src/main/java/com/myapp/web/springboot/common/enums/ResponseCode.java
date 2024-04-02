package com.myapp.web.springboot.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 공통 응답 dto용 결과코드
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum ResponseCode {
    SUCCESS, FAIL, ERROR;

    public static ResponseCode fromString(String text) {
        for(ResponseCode e : ResponseCode.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
