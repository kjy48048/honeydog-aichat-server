package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.Tools;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 메세지 실행 요청 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *       "role": "user",
 *       "content": "How does AI work? Explain it in simple terms."
 *     }
 */
@Data
@NoArgsConstructor
public class RunsRequestDto {
    @JsonProperty("assistant_id")
    private String assistantId;
    private String model;
    private String instructions;
    @JsonProperty("additional_instructions")
    private String additionalInstructions;
    private List<Tools> tools;
    private Map<String, Object> metadata;

    @Builder
    public RunsRequestDto(String assistantId, String model, String instructions, String additionalInstructions, List<Tools> tools, Map<String, Object> metadata) {
        this.assistantId = assistantId;
        this.model = model;
        this.instructions = instructions;
        this.additionalInstructions = additionalInstructions;
        this.tools = tools;
        this.metadata = metadata;
    }
}
