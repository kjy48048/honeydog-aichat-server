package com.myapp.web.springboot.functionchat.dto;


import com.myapp.web.springboot.functionchat.enums.Tarot;
import lombok.Data;

import java.util.Random;

/**
 * <pre>
 *     설명: 랜덤 타로 응답 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 22.
 * </pre>
 */
@Data
public class TarotResponseDto {
    private String name;
    private String korName;
    private String type;
    private String imageUrl;
    private String keyword;
    private String description;
    private String position; // (true: 정위치, false:역위치)
    private String normalPositionMean;
    private String reversePositionMean;
    private Long index; // 순서

    public TarotResponseDto(Tarot tarot, Long index) {
        this.name = tarot.name();
        this.korName = tarot.getKorName();
        this.type = tarot.getType();
        this.imageUrl = "https://honeydog.co.kr" + tarot.getImageUrl();
        this.keyword = tarot.getKeyword();
        this.description = tarot.getDescription();
        this.position = new Random().nextBoolean() ? "normal" : "reverse";
        this.normalPositionMean = tarot.getNormalPositionMean();
        this.reversePositionMean = tarot.getReversePositionMean();
        this.index = index;
    }
}
