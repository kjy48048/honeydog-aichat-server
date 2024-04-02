package com.myapp.web.springboot.recommend.dto;

import com.myapp.web.springboot.recommend.domain.Recommend;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class RecommendWebResponseDto {
    private Long id;
    private String nick;
    private String question;
    private String color;
    private Long orderIndex;
    private LocalDateTime modifiedDate;

    public String getFormattedModifiedDate() {
        return modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public RecommendWebResponseDto(Recommend recommend) {
        this.id = recommend.getRecommendId();
        this.nick = recommend.getNick();
        this.question = recommend.getQuestion();
        this.color = recommend.getColor();
        this.orderIndex = recommend.getOrderIndex();
        this.modifiedDate = recommend.getModifiedDate();
    }
}
