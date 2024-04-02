package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Usage {
    @JsonProperty("completion_tokens")
    private Long completionToken;
    @JsonProperty("prompt_tokens")
    private Long promptTokens;
    @JsonProperty("total_tokens")
    private Long totalTokens;
}
