package com.myapp.web.springboot.assistant.dto.field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Function {
    private String description;
    private String name;
    private Object parameters;
}
