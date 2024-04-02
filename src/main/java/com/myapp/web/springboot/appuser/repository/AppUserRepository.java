package com.myapp.web.springboot.appuser.repository;

import com.myapp.web.springboot.appuser.domain.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 *  <pre>
 *      설명: 앱용 유저 관리 리포지토리
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 20.
 *  </pre>
 */
public interface AppUserRepository extends JpaRepository<AppUser, UUID>, JpaSpecificationExecutor<AppUser> {
    Optional<AppUser> findByEmail(String email);
    Boolean existsByEmail(String email);

    @Query("SELECT au FROM AppUser au WHERE au.appUserRole = com.myapp.web.springboot.appuser.enums.AppUserRole.SYSTEM")
    Optional<AppUser> findSystemUser();

    @Query("SELECT au FROM AppUser au WHERE au.userUuid = :userUuid")
    AppUser findByUserUuid(@Param("userUuid") UUID userUuid);

    @Query("SELECT au FROM AppUser au " +
            "WHERE au.nick = :nick " +
            "AND au.appUserRole = com.myapp.web.springboot.appuser.enums.AppUserRole.AI")
    AppUser findAiUserByNick(@Param("nick") String nick);

    @Query("SELECT au FROM AppUser au " +
            "WHERE au.appUserRole = com.myapp.web.springboot.appuser.enums.AppUserRole.AI " +
            "AND au.appUserStatus = com.myapp.web.springboot.appuser.enums.AppUserStatus.NORMAL ")
    List<AppUser> findAiUsers();

    @Query("SELECT au FROM AppUser au " +
            "WHERE au.userUuid <> :userUuid " +
            "AND au.appUserRole = com.myapp.web.springboot.appuser.enums.AppUserRole.USER " +
            "AND au.appUserStatus = com.myapp.web.springboot.appuser.enums.AppUserStatus.NORMAL " +
            "AND au.nick LIKE %:nick%")
    Page<AppUser> findUserByNickLikeNotMe(@Param("userUuid") UUID userUuid, @Param("nick") String nick, Pageable pageable);

    @Query("SELECT au FROM AppUser au " +
            "WHERE au.userUuid <> :userUuid " +
            "AND au.appUserRole = com.myapp.web.springboot.appuser.enums.AppUserRole.USER " +
            "AND au.appUserStatus = com.myapp.web.springboot.appuser.enums.AppUserStatus.NORMAL " +
            "AND au.email LIKE %:email%")
    Page<AppUser> findUserByEmailLikeNotMe(@Param("userUuid") UUID userUuid, @Param("email") String email, Pageable pageable);


    @Query("SELECT au FROM AppUser au JOIN AppUserRoom aur ON au.userUuid = aur.appUser.userUuid " +
            "WHERE aur.appRoom.roomUuid = :roomUuid AND aur.userRoomRole = com.myapp.web.springboot.appuserroom.enums.UserRoomRole.AI")
    List<AppUser> findAiUsersByRoomUuid(@Param("roomUuid") UUID roomUuid);
}
