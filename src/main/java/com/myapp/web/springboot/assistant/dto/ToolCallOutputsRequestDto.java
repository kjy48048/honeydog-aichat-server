package com.myapp.web.springboot.assistant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.myapp.web.springboot.assistant.dto.field.ToolOutputs;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 *     설명: 오픈AI Function 채팅 tools Outputs...
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 * {
 *     "tool_outputs": [
 *         {
 *             "tool_call_id": "call_n8LuimOvdb8yw2l81FFCSUSb",
 *             "output": "{~}"
 *         }
 *     ]
 * }
 */
@Data
@NoArgsConstructor
public class ToolCallOutputsRequestDto {
    @JsonProperty("tool_outputs")
    List<ToolOutputs> toolOutputs;
}
