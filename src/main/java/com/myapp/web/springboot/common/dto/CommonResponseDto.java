package com.myapp.web.springboot.common.dto;

import lombok.Data;

/**
 * <pre>
 *     설명: 공통 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2023. 11. 02.
 * </pre>
 */
@Data
public class CommonResponseDto {
    private String rspMsg;  // 결과 메세지
    private String rspCode; // 결과 코드(200 정상, 기타 에러...)
}
