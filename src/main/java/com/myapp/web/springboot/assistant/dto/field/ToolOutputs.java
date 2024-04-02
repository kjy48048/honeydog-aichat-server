package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ToolOutputs {
    @JsonProperty("tool_outputs")
    private List<ToolOutput> toolOutputs;
}
