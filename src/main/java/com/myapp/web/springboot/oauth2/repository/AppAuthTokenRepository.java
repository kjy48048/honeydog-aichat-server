package com.myapp.web.springboot.oauth2.repository;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.oauth2.domain.AppAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *  <pre>
 *      설명: 앱용 유저 토큰 저장 리포지토리
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 16.
 *  </pre>
 */
public interface AppAuthTokenRepository extends JpaRepository<AppAuthToken, Long> {
    // 살아있는 일치하지 않는 토큰 조회
    @Query("SELECT aat FROM AppAuthToken aat " +
            "WHERE aat.appUser = :appUser and not aat.deviceId = :deviceId " +
            "AND aat.tokenStatus = com.myapp.web.springboot.oauth2.enums.TokenStatus.ALIVE AND aat.expiredDate >= sysdate() ")
    List<AppAuthToken> findAppAuthTokensByAppUserAndNotDeviceId(@Param("appUser") AppUser appUser, @Param("deviceId") String deviceId);

    // 디바이스ID로 살아있는 일치하는 토큰 조회(로그아웃시 업데이트 용)
    @Query("SELECT aat FROM AppAuthToken aat " +
            "WHERE aat.deviceId = :deviceId " +
            "AND aat.tokenStatus = com.myapp.web.springboot.oauth2.enums.TokenStatus.ALIVE ")
    List<AppAuthToken> findAliveAppAuthTokenByDeviceId(@Param("deviceId") String deviceId);

    @Query("SELECT aat FROM AppAuthToken aat WHERE aat.authToken = :authToken ")
    List<AppAuthToken> findAppAuthTokensByAuthToken(@Param("authToken") String authToken);

    // 살아 있는 토큰 조회. 토큰만료일 체크X  AND aat.expiredDate >= sysdate()
    @Query("SELECT aat FROM AppAuthToken aat " +
            "JOIN AppUser au ON aat.appUser.userUuid = au.userUuid " +
            "WHERE aat.authToken = :authToken AND aat.deviceId IN ('TEMP', :deviceId) AND au.email = :email " +
            "AND aat.tokenStatus = com.myapp.web.springboot.oauth2.enums.TokenStatus.ALIVE ")
    List<AppAuthToken> findAliveAppAuthTokensByAuthTokenAndDeviceIdAndEmail(@Param("authToken") String authToken,
                                       @Param("deviceId") String deviceId, @Param("email") String email);
}
