package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ToolOutput {
    @JsonProperty("tool_call_id")
    private String toolCallId;
    private String output;
}
