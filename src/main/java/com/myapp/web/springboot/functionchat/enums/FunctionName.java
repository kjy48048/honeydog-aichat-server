package com.myapp.web.springboot.functionchat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 타로 function 매칭용...
 *     작성자: kimjinyoung
 *     작성일: 2024. 3. 25.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum FunctionName {
    RANDOM_TAROT("get_random_tarot");

    private final String functionName;

    public static FunctionName fromString(String text) {
        for(FunctionName e : FunctionName.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }

    public static FunctionName fromFunctionName(String text) {
        for(FunctionName e : FunctionName.values()) {
            if(e.getFunctionName().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }
}
