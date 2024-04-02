package com.myapp.web.springboot.recommend.service;

import com.myapp.web.springboot.aichatter.domain.AiChatter;
import com.myapp.web.springboot.aichatter.enums.AccessRole;
import com.myapp.web.springboot.aichatter.service.AiChatterService;
import com.myapp.web.springboot.recommend.domain.Recommend;
import com.myapp.web.springboot.recommend.dto.RecommendResponseDto;
import com.myapp.web.springboot.recommend.dto.RecommendSaveRequestDto;
import com.myapp.web.springboot.recommend.dto.RecommendWebResponseDto;
import com.myapp.web.springboot.recommend.enums.BasicRecommend;
import com.myapp.web.springboot.recommend.repository.RecommendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  <pre>
 *      설명: 추천 질문 관련 서비스
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 17.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class RecommendService {
    private final AiChatterService aiChatterService;
    private final RecommendRepository recommendRepository;

    @Transactional
    public List<RecommendResponseDto> getList() {
        try {
            List<Recommend> recommendList = recommendRepository.findAll(Sort.by(Sort.Direction.DESC, "orderIndex"));
            List<AiChatter> publicAiChatters = aiChatterService.findListByAccessRole(AccessRole.PUBLIC);
            List<RecommendResponseDto> responseDtoList = new ArrayList<>();

            //없을 경우 기본 생성
            if(recommendList.size() == 0) {
                for(BasicRecommend basicRecommend : BasicRecommend.values()) {
                    Recommend recommend = Recommend.builder()
                            .nick(basicRecommend.getNick())
                            .question(basicRecommend.getQuestion())
                            .color(basicRecommend.getColor())
                            .build();

                    recommend = recommendRepository.save(recommend);
                    RecommendResponseDto responseDto = new RecommendResponseDto(recommend);
                    responseDtoList.add(responseDto);
                }
            } else {
                responseDtoList = recommendList
                        .stream()
                        .map(RecommendResponseDto::new)
                        .collect(Collectors.toList());
            }

            for(RecommendResponseDto responseDto : responseDtoList) {
                publicAiChatters.forEach(publicAiChatter -> {
                    if(publicAiChatter.getNick().equals(responseDto.getNick())) {
                        responseDto.setUserUuid(publicAiChatter.getAiUser().getUserUuid().toString());
                        responseDto.setGreetings(publicAiChatter.getAiUser().getGreetings());
                        responseDto.setPicture(publicAiChatter.getAiUser().getPicture());
                    }
                });
            }

            return responseDtoList;
        } catch (Exception e) {
            log.error("getList error: {}", e.getMessage(), e);
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<RecommendWebResponseDto> findAll() {
        return recommendRepository.findAll().stream()
                .map(RecommendWebResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long save(RecommendSaveRequestDto requestDto) {
        return recommendRepository.save(requestDto.toEntity()).getRecommendId();
    }

    @Transactional
    public Long update(Long id, RecommendSaveRequestDto requestDto) {
        Recommend recommend = recommendRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        recommendRepository.save(recommend.update(requestDto));

        return id;
    }

    public RecommendWebResponseDto findById(Long id) {
        Recommend recommend = recommendRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        return new RecommendWebResponseDto(recommend);
    }

    @Transactional
    public void delete(Long id) {
        Recommend recommend = recommendRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id= " + id));
        recommendRepository.delete(recommend);
    }
}
