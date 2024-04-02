package com.myapp.web.springboot.appuserroom.repository;

import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *     설명: 앱용 채팅방 리파지토리
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
public interface AppUserRoomRepository extends JpaRepository<AppUserRoom, Long> {
    /**
     * 채팅방 uuid로 채팅방 안의 존재하는 유저 리스트 조회
     * @param roomUuid 방 uuid
     * @param userRoomStatus 채팅방 상태(참가함, 나감)
     * @return 채팅방 안의 존재하는 유저 리스트 조회
     */
    @Query("SELECT aur FROM AppUserRoom aur WHERE aur.appRoom.roomUuid = :roomUuid AND aur.userRoomStatus = :userRoomStatus")
    List<AppUserRoom> findAppUserRoomsByRoomUuid(@Param("roomUuid") UUID roomUuid, @Param("userRoomStatus") UserRoomStatus userRoomStatus);

    /**
     * 유저 uuid로 아직 참여한 목록 최신순 조회
     * @param userRoomStatus 채팅방 상태(참가함, 나감)
     * @return 유저가 참여한 채팅방 리스트
     */
    @Query("SELECT aur FROM AppUserRoom aur WHERE aur.appUser.userUuid = :userUuid AND aur.userRoomStatus = :userRoomStatus ORDER BY aur.modifiedDate desc")
    List<AppUserRoom> findAppUserRoomsByUserUuidAndRoomStatus(@Param("userUuid")UUID userUuid, @Param("userRoomStatus") UserRoomStatus userRoomStatus);

    /**
     * 유저 uuid와 채팅방 uuid로 유저-채팅방 관계 조회
     * @param userUuid 유저 uuid
     * @param roomUuid 방 uuid
     * @return 유저-채팅방 관계 조회
     */
    @Query("SELECT aur FROM AppUserRoom aur WHERE aur.appUser.userUuid = :userUuid AND aur.appRoom.roomUuid = :roomUuid")
    AppUserRoom findAppUserRoomByRoomUuidAndUserUuid(@Param("userUuid")UUID userUuid, @Param("roomUuid") UUID roomUuid);


    /**
     * 유저 uuid와 채팅방 uuid로 유저-채팅방 관계 조회
     * @param roomUuid 방 uuid
     * @return 유저-채팅방 관계 조회
     */
    @Query("SELECT aur FROM AppUserRoom aur WHERE aur.appRoom.roomUuid = :roomUuid AND aur.userRoomRole = com.myapp.web.springboot.appuserroom.enums.UserRoomRole.AI")
    List<AppUserRoom> findAiUsersRoomByRoomUuid(@Param("roomUuid")UUID roomUuid);
}
