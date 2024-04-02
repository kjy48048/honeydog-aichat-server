package com.myapp.web.springboot.appchat.repository;

import com.myapp.web.springboot.appchat.domain.AppChatHistory;
import com.myapp.web.springboot.appchat.enums.AppMessageType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 *  <pre>
 *      설명: 앱용 유저 채팅 저장 리포지토리
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 23.
 *  </pre>
 */
public interface AppChatHistoryRepository extends JpaRepository<AppChatHistory, Long> {
    @Query("SELECT ach FROM AppChatHistory ach " +
            "INNER JOIN AppUserRoom aur ON ach.appRoom.roomUuid = aur.appRoom.roomUuid " +
            "WHERE aur.inDateTime <= ach.createdDate AND ach.appRoom.roomUuid = :roomUuid " +
            "ORDER BY ach.createdDate DESC")
    Page<AppChatHistory> findAllChatByRoomUuidDesc(@Param("roomUuid") UUID roomUuid, Pageable pageable);

    @Query("SELECT ach FROM AppChatHistory ach " +
            "INNER JOIN AppUserRoom aur ON ach.appRoom.roomUuid = aur.appRoom.roomUuid " +
            "WHERE aur.inDateTime <= ach.createdDate AND ach.appRoom.roomUuid = :roomUuid AND ach.messageType = :messageType " +
            "ORDER BY ach.createdDate DESC")
    List<AppChatHistory> findEnterChatByRoomUuid(@Param("roomUuid")UUID roomUuid, @Param("messageType")AppMessageType messageType);

    @Query("SELECT ach FROM AppChatHistory ach WHERE ach.appRoom.roomUuid = :roomUuid " +
            "AND ach.messageType <> com.myapp.web.springboot.appchat.enums.AppMessageType.ENTER " +
            "ORDER BY ach.createdDate DESC")
    List<AppChatHistory> findAllChatNoneEnterByRoomUuid(@Param("roomUuid") UUID roomUuid);

}
