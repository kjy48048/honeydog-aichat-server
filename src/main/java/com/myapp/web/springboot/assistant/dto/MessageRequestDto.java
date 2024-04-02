package com.myapp.web.springboot.assistant.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 오픈AI 메세지 요청 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *    "role": "user",
 *    "content": "How does AI work? Explain it in simple terms."
 * }
 */
@Data
@NoArgsConstructor
public class MessageRequestDto {
    private String role;
    private String content;

    @Builder
    public MessageRequestDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}
