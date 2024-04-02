package com.myapp.web.springboot.approom.repository;

import com.myapp.web.springboot.approom.domain.AppRoom;
import com.myapp.web.springboot.approom.enums.AppRoomStatus;
import com.myapp.web.springboot.appuserroom.enums.UserRoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
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
public interface AppRoomRepository extends JpaRepository<AppRoom, UUID>, JpaSpecificationExecutor<AppRoom> {
    /**
     * 앱용 채팅방 uuid와 열린방 조회
     * @param roomUuid 방 uuid
     * @param roomStatus 채팅방 상태(열림, 닫힘, 만료됨)
     * @return 채팅방
     */
    @Query("SELECT ar FROM AppRoom ar WHERE ar.roomUuid = :roomUuid AND ar.roomStatus = :roomStatus")
    AppRoom findAppRoomByRoomUuidAndRoomStatus(@Param("roomUuid")UUID roomUuid, @Param("roomStatus") AppRoomStatus roomStatus);

    /**
     * 앱용 유저 참여한 방 조회
     * 유저-채팅방 조인,
     * @param userUuid 유저 uuid
     * @param roomStatus 채팅방 상태(열림, 닫힘, 만료됨)
     * @param userRoomStatus 유저-채팅방 상태(In, Out)
     * @return 채팅방
     */
    @Query("SELECT ar FROM AppRoom ar inner join AppUserRoom aur on ar.roomUuid = aur.appRoom.roomUuid " +
            "WHERE aur.appUser.userUuid = :userUuid AND ar.roomStatus = :roomStatus and aur.userRoomStatus = :userRoomStatus order by ar.modifiedDate desc")
    List<AppRoom> findJoinRooms(@Param("userUuid")UUID userUuid, @Param("roomStatus") AppRoomStatus roomStatus, @Param("userRoomStatus")UserRoomStatus userRoomStatus);

    /**
     *
     * @param userUuid 유저 uuid
     * @param roomStatus 채팅방 상태(열림, 닫힘, 만료됨)
     * @return 채팅방 목록
     */
    @Query("SELECT ar FROM AppRoom ar WHERE ar.roomUuid IN :roomUuids AND ar.roomStatus = :roomStatus")
    List<AppRoom> findAppRoomsByRoomUuidAndRoomStatus(@Param("roomUuids")List<UUID> userUuid, @Param("roomStatus") AppRoomStatus roomStatus);


    /**
     * 두 사용자가 모두 있는 채팅방 찾기
     * @param userUuid1 유저1 uuid
     * @param userUuid2 유저2 uuid
     * @return 모두 있는 채팅방 목록
     */
    @Query("SELECT ar " +
            "FROM AppRoom ar " +
            "WHERE ar.roomUuid " +
            "IN (SELECT aur1.appRoom.roomUuid " +
            "FROM AppUserRoom aur1 " +
            "JOIN AppUserRoom aur2 " +
            "ON aur1.appRoom.roomUuid = aur2.appRoom.roomUuid " +
            "WHERE aur1.appUser.userUuid = :userUuid1 " +
            "AND aur2.appUser.userUuid = :userUuid2 " +
            "AND aur1.userRoomStatus = com.myapp.web.springboot.appuserroom.enums.UserRoomStatus.IN " +
            "AND aur2.userRoomStatus = com.myapp.web.springboot.appuserroom.enums.UserRoomStatus.IN)" +
            "ORDER BY ar.modifiedDate DESC ")
    List<AppRoom> findRoomsWithTwoUsers(@Param("userUuid1")UUID userUuid1,@Param("userUuid2")UUID userUuid2);
}
