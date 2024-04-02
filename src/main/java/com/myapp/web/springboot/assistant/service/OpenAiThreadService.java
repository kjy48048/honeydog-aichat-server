package com.myapp.web.springboot.assistant.service;

import com.myapp.web.springboot.assistant.domain.OpenAiThread;
import com.myapp.web.springboot.assistant.repository.OpenAiThreadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *     설명: 오픈 AI 쓰레드 데이터 서비스
 *     작성자: kimjinyoung
 *     작성일: 2024. 2. 14.
 * </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class OpenAiThreadService {
    private final OpenAiThreadRepository openAiThreadRepository;

    public List<OpenAiThread> getList(String roomUuid) {
        return openAiThreadRepository.findLiveThreadByRoomUuid(UUID.fromString(roomUuid));
    }

    public OpenAiThread save(OpenAiThread openAiThread) {
        return openAiThreadRepository.save(openAiThread);
    }
}
