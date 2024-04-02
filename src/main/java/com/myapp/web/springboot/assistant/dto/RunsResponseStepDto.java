package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.LastError;
import com.myapp.web.springboot.assistant.dto.field.StepDetails;
import com.myapp.web.springboot.assistant.dto.field.Usage;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 *     설명: 오픈AI 메세지 실행 step 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 15.
 * </pre>
 * 예)
 * {
 *   "object": "list",
 *   "data": [
 *     {
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
 *   ],
 *   "first_id": "step_abc123",
 *   "last_id": "step_abc456",
 *   "has_more": false
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class RunsResponseStepDto extends CommonResponseDto {
    private String object;
    @JsonProperty("data")
    private List<RunsResponseStepData> runsResponseStepData;
    @JsonProperty("first_id")
    private String firstId;
    @JsonProperty("last_id")
    private String lastId;
    private String hasMore;
}
