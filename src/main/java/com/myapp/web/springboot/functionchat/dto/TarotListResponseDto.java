package com.myapp.web.springboot.functionchat.dto;

import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * <pre>
 *     설명: 랜덤 타로 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 22.
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TarotListResponseDto extends CommonResponseDto {
    private List<TarotResponseDto> tarotDeck;
    private Long size;
}
