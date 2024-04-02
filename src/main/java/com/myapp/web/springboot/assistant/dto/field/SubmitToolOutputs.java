package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class SubmitToolOutputs {
    @JsonProperty("tool_calls")
    private List<ToolCalls> toolCalls;
}
