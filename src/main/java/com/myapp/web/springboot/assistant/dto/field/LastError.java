package com.myapp.web.springboot.assistant.dto.field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LastError {
    private String code;
    private String message;
}
