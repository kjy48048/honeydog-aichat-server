package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.LastError;
import com.myapp.web.springboot.assistant.dto.field.RequiredAction;
import com.myapp.web.springboot.assistant.dto.field.Tools;
import com.myapp.web.springboot.assistant.dto.field.Usage;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     설명: 오픈AI 메세지 실행 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *   "id": "run_abc123",
 *   "object": "thread.run",
 *   "created_at": 1699063290,
 *   "assistant_id": "asst_abc123",
 *   "thread_id": "thread_abc123",
 *   "status": "queued",
 *   "started_at": 1699063290,
 *   "expires_at": null,
 *   "cancelled_at": null,
 *   "failed_at": null,
 *   "completed_at": 1699063291,
 *   "last_error": null,
 *   "model": "gpt-4",
 *   "instructions": null,
 *   "tools": [
 *     {
 *       "type": "code_interpreter"
 *     }
 *   ],
 *   "file_ids": [
 *     "file-abc123",
 *     "file-abc456"
 *   ],
 *   "metadata": {},
 *   "usage": null
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RunsResponseDto extends CommonResponseDto {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("thread_id")
    private String threadId;
    private String status; //queued, in_progressm requires_action, cancelling, cancelled, faild, completed, expired
    @JsonProperty("required_action")
    private RequiredAction requiredAction;
    @JsonProperty("started_at")
    private Long startAt;
    @JsonProperty("expires_at")
    private Long expiresAt;
    @JsonProperty("cancelled_at")
    private Long cancelledAt;
    @JsonProperty("failed_at")
    private Long failedAt;
    @JsonProperty("completed_at")
    private Long completedAt;
    @JsonProperty("last_error")
    private LastError lastError;
    private String model;
    private String instructions;
    private List<Tools> tools;
    private List<String> fileIds;
    private Map<String, Object> metadata;
    private Usage usage;
}
