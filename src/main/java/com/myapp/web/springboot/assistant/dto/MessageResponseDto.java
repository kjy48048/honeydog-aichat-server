package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 메세지 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *   "id": "msg_abc123",
 *   "object": "thread.message",
 *   "created_at": 1699017614,
 *   "thread_id": "thread_abc123",
 *   "role": "user",
 *   "content": [
 *     {
 *       "type": "text",
 *       "text": {
 *         "value": "How does AI work? Explain it in simple terms.",
 *         "annotations": []
 *       }
 *     }
 *   ],
 *   "file_ids": [],
 *   "assistant_id": null,
 *   "run_id": null,
 *   "metadata": {}
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MessageResponseDto extends CommonResponseDto {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("thread_id")
    private String threadId;
    private String role;
    List<MessageContentData> content;
    @JsonProperty("file_ids")
    private List<String> fileIds;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("run_id")
    private String runId;
    private Map<String, Object> metadata;
}
