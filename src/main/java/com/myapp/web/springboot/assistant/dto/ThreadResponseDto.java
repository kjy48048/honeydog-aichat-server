package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 쓰레드 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *   "id": "thread_abc123",
 *   "object": "thread",
 *   "created_at": 1699012949,
 *   "metadata": {}
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class ThreadResponseDto extends CommonResponseDto {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private Long createdAt;
    private Map<String, Object> metadata;
}
