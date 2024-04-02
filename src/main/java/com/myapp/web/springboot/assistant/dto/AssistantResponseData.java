package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.Tools;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 응답 DTO 내부 Data
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *   "id": "asst_abc123",
 *   "object": "assistant",
 *   "created_at": 1698984975,
 *   "name": "Math Tutor",
 *   "description": null,
 *   "model": "gpt-4",
 *   "instructions": "You are a personal math tutor. When asked a question, write and run Python code to answer the question.",
 *   "tools": [
 *     {
 *       "type": "code_interpreter"
 *     }
 *   ],
 *   "file_ids": [],
 *   "metadata": {}
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class AssistantResponseData extends CommonResponseDto {
    private String id; // The identifier, which can be referenced in API endpoints
    private String object;
    @JsonProperty("created_at")
    private Long createdAt;
    private String name;
    private String description;
    private String model;
    private String instructions;
    private List<Tools> tools;
    @JsonProperty("file_ids")
    private List<String> fileIds;
    private Map<String, Object> metadata;
}
