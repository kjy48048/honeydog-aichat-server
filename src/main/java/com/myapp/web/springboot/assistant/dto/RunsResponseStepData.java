package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.LastError;
import com.myapp.web.springboot.assistant.dto.field.StepDetails;
import com.myapp.web.springboot.assistant.dto.field.Usage;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 오픈AI 메세지 실행 step 응답 Data
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 15.
 * </pre>
 * 예)
 * {
 *       "id": "step_abc123",
 *       "object": "thread.run.step",
 *       "created_at": 1699063291,
 *       "run_id": "run_abc123",
 *       "assistant_id": "asst_abc123",
 *       "thread_id": "thread_abc123",
 *       "type": "message_creation",
 *       "status": "completed",
 *       "cancelled_at": null,
 *       "completed_at": 1699063291,
 *       "expired_at": null,
 *       "failed_at": null,
 *       "last_error": null,
 *       "step_details": {
 *         "type": "message_creation",
 *         "message_creation": {
 *           "message_id": "msg_abc123"
 *         }
 *       },
 *       "usage": {
 *         "prompt_tokens": 123,
 *         "completion_tokens": 456,
 *         "total_tokens": 579
 *       }
 *     }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RunsResponseStepData extends CommonResponseDto {
    private String id;
    private String object;
    @JsonProperty("created_at")
    private Long createdAt;
    @JsonProperty("run_id")
    private String runId;
    @JsonProperty("assistant_id")
    private String assistantId;
    @JsonProperty("thread_id")
    private String threadId;
    private String type;
    private String status; //queued, in_progressm requires_action, cancelling, cancelled, faild, completed, expired
    @JsonProperty("cancelled_at")
    private Long cancelledAt;
    @JsonProperty("completed_at")
    private Long completedAt;
    @JsonProperty("expired_at")
    private Long expiredAt;
    @JsonProperty("failed_at")
    private Long failedAt;
    @JsonProperty("last_error")
    private LastError lastError;
    @JsonProperty("step_details")
    private StepDetails stepDetails;
    private Usage usage;
}
