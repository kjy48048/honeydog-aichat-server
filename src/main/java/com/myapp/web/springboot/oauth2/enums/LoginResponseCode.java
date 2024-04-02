package com.myapp.web.springboot.oauth2.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 로그인 응답코드
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 16.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum LoginResponseCode {

    SUCCESS("200", "SUCCESS"),
    ERROR_INVALID_REQUEST("404", "INVALID_REQUEST"),
    ERROR_INTERNAL_SERVER_ERROR("500", "INTERNAL_SERVER_ERROR");

    private final String code;
    private final String message;
}
