package com.myapp.web.springboot.assistant.dto.field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ToolCalls {
    private String id;
    private String type;
    private ToolCallsFunction function;
}
