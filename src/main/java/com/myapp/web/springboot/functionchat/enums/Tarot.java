package com.myapp.web.springboot.functionchat.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * <pre>
 *     설명: 타로 기본 정보
 *     작성자: kimjinyoung
 *     작성일: 2024. 3. 22.
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum Tarot {
    THE_FOOL("", "", "", "", "", "", "");

    private final String korName;
    private final String type;
    private final String imageUrl;
    private final String keyword;
    private final String description;
    private final String normalPositionMean;
    private final String reversePositionMean;

    public static Tarot fromString(String text) {
        for(Tarot e : Tarot.values()) {
            if(e.name().equalsIgnoreCase(text)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 랜덤한 타로 카드를 반환하는 메서드
     */
    public static Tarot getRandomCard() {
        Random random = new Random();
        Tarot[] cards = Tarot.values();
        int randomIndex = random.nextInt(cards.length);
        return cards[randomIndex];
    }

    /**
     * 랜덤한 카드를 일정 수만큼 반환하는 메서드
     * @param numberOfCards 뽑을 카드 수
     * @return 랜덤한 카드목록
     */
    public static List<Tarot> getRandomCards(int numberOfCards) {
        Tarot[] cards = Tarot.values();
        List<Tarot> cardsList = new ArrayList<>(Arrays.asList(cards));
        Collections.shuffle(cardsList);
        return cardsList.subList(0, Math.min(numberOfCards, cardsList.size()));
    }

}
