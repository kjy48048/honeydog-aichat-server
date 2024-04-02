package com.myapp.web.springboot.assistant.dto.field;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StepDetails {
    private String type;
    @JsonProperty("message_creation")
    private MessageCreation messageCreation;
}
