package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequiredAction {
    private String type;
    @JsonProperty("submit_tool_outputs")
    private SubmitToolOutputs submitToolOutputs;
}
