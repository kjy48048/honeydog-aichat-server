package com.myapp.web.springboot.recommend.dto;

import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;


/**
 * <pre>
 *     설명: ai 채터 응답용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecommendResponseListDto extends CommonResponseDto {
    public List<RecommendResponseDto> body;
}
