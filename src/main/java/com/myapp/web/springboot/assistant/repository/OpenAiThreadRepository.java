package com.myapp.web.springboot.assistant.repository;

import com.myapp.web.springboot.assistant.domain.OpenAiThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *     설명: 오픈 AI 쓰레드관리 리파지토리
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 14.
 * </pre>
 */
public interface OpenAiThreadRepository extends JpaRepository<OpenAiThread, String> {
    @Query("SELECT oat FROM OpenAiThread oat WHERE oat.appRoom.roomUuid = :roomUuid AND oat.threadStatus = com.myapp.web.springboot.assistant.enums.ThreadStatus.LIVE")
    List<OpenAiThread> findLiveThreadByRoomUuid(@Param("roomUuid") UUID roomUuid);

}
