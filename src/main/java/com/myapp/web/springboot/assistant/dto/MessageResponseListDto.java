package com.myapp.web.springboot.assistant.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 *     설명: 오픈AI 메세지 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *   "object": "list",
 *   "data": [
 *     {
 *       "id": "msg_abc123",
 *       "object": "thread.message",
 *       "created_at": 1699016383,
 *       "thread_id": "thread_abc123",
 *       "role": "user",
 *       "content": [
 *         {
 *           "type": "text",
 *           "text": {
 *             "value": "How does AI work? Explain it in simple terms.",
 *             "annotations": []
 *           }
 *         }
 *       ],
 *       "file_ids": [],
 *       "assistant_id": null,
 *       "run_id": null,
 *       "metadata": {}
 *     },
 *     {
 *       "id": "msg_abc456",
 *       "object": "thread.message",
 *       "created_at": 1699016383,
 *       "thread_id": "thread_abc123",
 *       "role": "user",
 *       "content": [
 *         {
 *           "type": "text",
 *           "text": {
 *             "value": "Hello, what is AI?",
 *             "annotations": []
 *           }
 *         }
 *       ],
 *       "file_ids": [
 *         "file-abc123"
 *       ],
 *       "assistant_id": null,
 *       "run_id": null,
 *       "metadata": {}
 *     }
 *   ],
 *   "first_id": "msg_abc123",
 *   "last_id": "msg_abc456",
 *   "has_more": false
 * }
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class MessageResponseListDto extends CommonResponseDto {
    private String object;
    private List<MessageResponseDto> data;
    @JsonProperty("first_id")
    private String firstId;
    @JsonProperty("last_id")
    private String lastId;
    private boolean hasMore;
}
