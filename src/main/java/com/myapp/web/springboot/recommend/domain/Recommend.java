package com.myapp.web.springboot.recommend.domain;


import com.myapp.web.springboot.common.domain.BaseEntity;
import com.myapp.web.springboot.recommend.dto.RecommendSaveRequestDto;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *      설명: 추천질문
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 17.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class Recommend extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long recommendId;
    String nick;
    String question;
    String color;
    Long orderIndex;

    @Builder
    public Recommend(Long recommendId, String nick, String question, String color, Long orderIndex) {
        this.recommendId = recommendId;
        this.nick = nick;
        this.question = question;
        this.color = color;
        this.orderIndex = orderIndex;
    }

    public Recommend update(RecommendSaveRequestDto responseDto) {
        this.nick = responseDto.getNick();
        this.question = responseDto.getQuestion();
        this.color = responseDto.getColor();
        this.orderIndex = responseDto.getOrderIndex();
        return this;
    }
}
