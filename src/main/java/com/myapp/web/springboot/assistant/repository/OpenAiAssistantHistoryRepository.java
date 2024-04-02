package com.myapp.web.springboot.assistant.repository;

import com.myapp.web.springboot.assistant.domain.OpenAiAssistantHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * <pre>
 *     설명: 오픈 AI 어시스턴트 API 요청 내역 기록 리파지토리
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
public interface OpenAiAssistantHistoryRepository extends JpaRepository<OpenAiAssistantHistory, Long> {
}
