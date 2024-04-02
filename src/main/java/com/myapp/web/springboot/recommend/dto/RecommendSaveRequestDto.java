package com.myapp.web.springboot.recommend.dto;

import com.myapp.web.springboot.recommend.domain.Recommend;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendSaveRequestDto {
    Long recommendId;
    String nick;
    String question;
    String color;
    Long orderIndex;

    public Recommend toEntity() {
        return Recommend.builder()
                .recommendId(recommendId)
                .nick(nick)
                .question(question)
                .color(color)
                .orderIndex(orderIndex)
                .build();
    }
}
