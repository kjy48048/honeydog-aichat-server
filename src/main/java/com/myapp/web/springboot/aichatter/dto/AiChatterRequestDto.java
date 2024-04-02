package com.myapp.web.springboot.aichatter.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 *     설명: ai 채터 저장요청 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class AiChatterRequestDto {
    private String userUuid;
    private String picture;
    private String greetings;
    private String nick;
    private String model;
    private String openAiToken;
    private String accessRole;
}
