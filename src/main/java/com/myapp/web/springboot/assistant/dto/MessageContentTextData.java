package com.myapp.web.springboot.assistant.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <pre>
 *     설명: 오픈AI 어시스턴트 메세지 DTO 내부 content
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 13.
 * </pre>
 * 예)
 *   {
 *      "value": "How does AI work? Explain it in simple terms.",
 *      "annotations": []
 *   }
 */
@Data
@NoArgsConstructor
public class MessageContentTextData {
    private String value;
    private List<Object> annotations;
}
