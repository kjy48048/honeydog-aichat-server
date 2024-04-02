package com.myapp.web.springboot.assistant.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * <pre>
 *     설명: 오픈AI 쓰레드 상태관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum ThreadStatus {
    LIVE, DEAD
}
