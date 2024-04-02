package com.myapp.web.springboot.appuser.domain;

import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * <pre>
 *     설명: 앱용 유저 도메인
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 20.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AppUser extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "VARCHAR(36)")
    @JdbcTypeCode(SqlTypes.VARCHAR)
    private UUID userUuid;

    @Column
    private String nick;

    @Column
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppUserRole appUserRole;

    private String greetings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppUserStatus appUserStatus;

    @Builder
    public AppUser(UUID userUuid, String nick, String email, String picture, AppUserRole appUserRole, String greetings, AppUserStatus appUserStatus) {
        this.userUuid = userUuid;
        this.nick = nick;
        this.email = email;
        this.picture = picture;
        this.appUserRole = appUserRole;
        this.greetings = greetings;
        this.appUserStatus = appUserStatus;
    }

    /**
     * 탈퇴 처리하기 위해 정보 초기화
     * @return 앱유저
     */
    public AppUser withdrawUser() {
        this.nick = "탈퇴한 유저";
        this.email = "";
        this.picture = "";
        this.greetings = "";
        this.appUserStatus = AppUserStatus.WITHDRAW;
        return this;
    }

    public AppUser update(String name, String picture) {
        this.nick = name;
        this.picture = picture;
        return this;
    }
    public AppUser updateNick(String nick) {
        this.nick = nick;
        return this;
    }

    public AppUser updatePicture(String picture) {
        this.picture = picture;
        return this;
    }

    public AppUser updateGreetings(String greetings) {
        this.greetings = greetings;
        return this;
    }

    public AppUser updateAppUserStatus(AppUserStatus appUserStatus) {
        this.appUserStatus = appUserStatus;
        return this;
    }
    public String getRoleKey() {
        return this.getAppUserRole().name();
    }
}
