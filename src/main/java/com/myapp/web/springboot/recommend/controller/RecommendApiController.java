package com.myapp.web.springboot.recommend.controller;

import com.myapp.web.springboot.common.enums.ResponseCode;
import com.myapp.web.springboot.recommend.dto.RecommendResponseDto;
import com.myapp.web.springboot.recommend.dto.RecommendResponseListDto;
import com.myapp.web.springboot.recommend.dto.RecommendSaveRequestDto;
import com.myapp.web.springboot.recommend.dto.RecommendWebResponseDto;
import com.myapp.web.springboot.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     설명: 추천질문 가져오기
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 17.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/api/v2/app/recommend")
public class RecommendApiController {
    private final RecommendService recommendService;

    /**
     * ai유저 정보 조회
     */
    @GetMapping("/list")
    public ResponseEntity<RecommendResponseListDto> findAllRecommend() {
        List<RecommendResponseDto> responseDtoList = recommendService.getList();
        RecommendResponseListDto responseListDto = new RecommendResponseListDto();

        if(responseDtoList == null || responseDtoList.size() == 0) {
            responseListDto.setRspCode(ResponseCode.FAIL.name());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseListDto);
        } else {
            responseListDto.setRspCode(ResponseCode.SUCCESS.name());
            responseListDto.setBody(responseDtoList);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseListDto);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Long> save(@RequestBody RecommendSaveRequestDto requestDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(recommendService.save(requestDto));
        } catch (Exception e) {
            log.error("posts save error, requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody RecommendSaveRequestDto requestDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(recommendService.update(id, requestDto));
        } catch (Exception e) {
            log.error("posts update error, requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecommendWebResponseDto> findById(@PathVariable Long id) {
        RecommendWebResponseDto responseDto = recommendService.findById(id);
        return responseDto == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        try {
            recommendService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (Exception e) {
            log.error("posts delete error, id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
