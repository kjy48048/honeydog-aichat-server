package com.myapp.web.springboot.assistant.dto.field;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Tools {
    private String type;
    private Function function;
}
