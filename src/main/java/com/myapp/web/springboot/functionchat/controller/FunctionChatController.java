package com.myapp.web.springboot.functionchat.controller;

import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.functionchat.dto.TarotListResponseDto;
import com.myapp.web.springboot.functionchat.dto.TarotResponseDto;
import com.myapp.web.springboot.functionchat.service.TarotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     설명: Open Ai Assistants API Function 관련 기능을 위한 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 22.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
public class FunctionChatController {

    private final TarotService tarotService;

    /**
     * 랜덤한 카드 목록 조회
     * @param requestNumber 요청받은 카드 수
     * @return 랜덤한 카드 목록
     */
    @GetMapping("/api/v3/random-tarot")
    public ResponseEntity<TarotListResponseDto> getRandomTarotCards(@RequestParam Long requestNumber) {

        try {
            List<TarotResponseDto> tarotList = new ArrayList<>();
            for(long i = 0L; i < requestNumber; i ++) {
                TarotResponseDto tarot = tarotService.getRandomTarot(i+1);
                tarotList.add(tarot);
            }
            TarotListResponseDto responseDto = new TarotListResponseDto();
            responseDto.setTarotDeck(tarotList);
            responseDto.setSize((long) tarotList.size());
            responseDto.setRspCode(ResponseCode.SUCCESS.name());

            return ResponseEntity.status(HttpStatus.OK).body(responseDto);
        } catch (Exception e) {
            log.error("getRandomTarotCards error : {}",e.getMessage(), e);
            TarotListResponseDto responseDto = new TarotListResponseDto();
            responseDto.setRspCode(ResponseCode.ERROR.name());
            responseDto.setRspMsg(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
        }
    }
}
