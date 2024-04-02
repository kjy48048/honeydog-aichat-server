package com.myapp.web.springboot.recommend.dto;

import com.myapp.web.springboot.recommend.domain.Recommend;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RecommendResponseDto {
    private String userUuid;
    private String greetings;
    private String picture;
    private String nick;
    private String question;
    private String color;
    private Long orderIndex;

    public RecommendResponseDto(Recommend recommend) {
        this.nick = recommend.getNick();
        this.question = recommend.getQuestion();
        this.color = recommend.getColor();
        this.orderIndex = recommend.getOrderIndex();
    }
}
