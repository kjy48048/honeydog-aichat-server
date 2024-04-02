package com.myapp.web.springboot.functionchat.service;

import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.functionchat.dto.TarotListResponseDto;
import com.myapp.web.springboot.functionchat.dto.TarotResponseDto;
import com.myapp.web.springboot.functionchat.enums.Tarot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 *      설명: 타로 서비스
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 22.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class TarotService {

    /**
     * 랜덤한 타로 카드 한장을 얻는다
     * @param index 카드 순서
     * @return 타로카드 응답용 DTO
     */
    public TarotResponseDto getRandomTarot(Long index) {
        return new TarotResponseDto(Tarot.getRandomCard(), index);
    }

    /**
     * 랜덤한 타로 카드를 number 수만큼을 얻는다
     * @param number 카드 순서
     * @return 타로카드 응답용 DTO
     */
    public TarotListResponseDto getRandomTarots(Long number) {
        TarotListResponseDto tarotListResponseDto = new TarotListResponseDto();
        AtomicLong index = new AtomicLong(1);
        try {
            List<TarotResponseDto> tarotResponseDtos = Tarot.getRandomCards(number.intValue()).stream()
                    .map(tarot -> new TarotResponseDto(tarot, index.getAndIncrement()))
                    .toList();
            tarotListResponseDto.setTarotDeck(tarotResponseDtos);
            tarotListResponseDto.setRspCode(ResponseCode.SUCCESS.name());
        } catch (Exception e) {
            log.error("getRandomTarots error... e: {}", e.getMessage(), e);
            tarotListResponseDto.setRspCode(ResponseCode.ERROR.name());
            tarotListResponseDto.setRspMsg("랜덤한 타로를 얻는 도중에 에러가 발생했습니다.");
        }

        return tarotListResponseDto;
    }
}
