package com.myapp.web.springboot.friend.repository;


import com.myapp.web.springboot.friend.domain.Friend;
import com.myapp.web.springboot.friend.enums.FriendStatus;
import com.myapp.web.springboot.friend.enums.FriendType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FriendRepository extends JpaRepository<Friend, Long> {
    @Query("SELECT f FROM Friend f " +
            "JOIN Friend f2 ON f.friendUser.userUuid = f2.user.userUuid " +
            "AND f2.friendUser.userUuid = f.user.userUuid " +
            "WHERE f.user.userUuid = :userUuid " +
            "AND f.friendStatus = com.myapp.web.springboot.friend.enums.FriendStatus.ACCEPTED " +
            "AND f.friendType = com.myapp.web.springboot.friend.enums.FriendType.HUMAN " +
            "AND f2.friendStatus = com.myapp.web.springboot.friend.enums.FriendStatus.ACCEPTED " +
            "AND f2.friendType = com.myapp.web.springboot.friend.enums.FriendType.HUMAN " +
            "ORDER BY f.friendUser.nick ASC")
    List<Friend> findInterFriendListByUser(@Param("userUuid") UUID userUuid);

    @Query("SELECT f FROM Friend f " +
            "JOIN Friend f2 ON f.friendUser.userUuid = f2.user.userUuid " +
            "AND f2.friendUser.userUuid = f.user.userUuid " +
            "WHERE f.user.userUuid = :userUuid " +
            "AND f.friendStatus = com.myapp.web.springboot.friend.enums.FriendStatus.ACCEPTED " +
            "AND f.friendType = com.myapp.web.springboot.friend.enums.FriendType.HUMAN " +
            "AND f2.friendStatus = com.myapp.web.springboot.friend.enums.FriendStatus.ACCEPTED " +
            "AND f2.friendType = com.myapp.web.springboot.friend.enums.FriendType.HUMAN " +
            "AND f.friendUser.userUuid NOT IN " +
            "(SELECT aur.appUser.userUuid FROM AppUserRoom aur " +
            "WHERE aur.appRoom.roomUuid = :roomUuid " +
            "AND aur.userRoomStatus = com.myapp.web.springboot.appuserroom.enums.UserRoomStatus.OUT) " +
            "ORDER BY f.friendUser.nick ASC")
    List<Friend> findInterFriendListByUserNotInRoom(@Param("userUuid") UUID userUuid, @Param("roomUuid") UUID roomUuid);
    @Query("SELECT f FROM Friend f WHERE f.user.userUuid = :userUuid")
    List<Friend> findAllRelationsShipByUser(@Param("userUuid") UUID userUuid);

    @Query("SELECT f FROM Friend f " +
            "WHERE f.user.userUuid = :userUuid " +
            "AND f.friendStatus = :friendStatus " +
            "AND f.friendType = :friendType " +
            "ORDER BY f.friendUser.nick ASC")
    List<Friend> findFriendListByUserAndStatusAndType(
            @Param("userUuid") UUID userUuid, @Param("friendStatus") FriendStatus friendStatus, @Param("friendType") FriendType friendType);

    @Query("SELECT f FROM Friend f " +
            "WHERE f.friendUser.userUuid = :userUuid " +
            "AND f.friendStatus = :friendStatus " +
            "AND f.friendType = :friendType " +
            "ORDER BY f.friendUser.nick ASC")
    List<Friend> findReverseFriendListByUserAndStatusAndType(
            @Param("userUuid") UUID userUuid, @Param("friendStatus") FriendStatus friendStatus, @Param("friendType") FriendType friendType);

    @Query("SELECT f FROM Friend f " +
            "WHERE f.user.userUuid = :userUuid " +
            "AND f.friendUser.userUuid = :friendUserUuid")
    Optional<Friend> findFriendByUserAndFriendUser(
            @Param("userUuid") UUID userUuid, @Param("friendUserUuid") UUID friendUserUuid);
}
