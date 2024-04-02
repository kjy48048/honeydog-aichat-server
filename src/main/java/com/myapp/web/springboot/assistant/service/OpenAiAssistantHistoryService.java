package com.myapp.web.springboot.assistant.service;

import com.myapp.web.springboot.assistant.domain.OpenAiAssistantHistory;
import com.myapp.web.springboot.assistant.repository.OpenAiAssistantHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * <pre>
 *     설명: 오픈 AI API 요청 히스토리 기록 서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 2. 14.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAiAssistantHistoryService {
    private final OpenAiAssistantHistoryRepository openAiAssistantHistoryRepository;
    public OpenAiAssistantHistory save(OpenAiAssistantHistory openAiAssistantHistory) {
        return openAiAssistantHistoryRepository.save(openAiAssistantHistory);
    }
}
