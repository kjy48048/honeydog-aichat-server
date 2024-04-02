package com.myapp.web.springboot.config.auth.dto;

import com.myapp.web.springboot.appuser.domain.AppUser;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

/**
 * <pre>
 *     설명: 세션유저
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@Getter
public class SessionUser implements Serializable {

    private UUID userId;
    private String name;
    private String email;
    private String picture;

    private String role;

    public SessionUser(AppUser user) {
        this.userId = user.getUserUuid();
        this.name = user.getNick();
        this.email = user.getEmail();
        this.picture = user.getPicture();
        if(user.getAppUserRole() != null) this.role = user.getAppUserRole().name();
    }

    public AppUser toEntity() {
        return AppUser.builder()
                .userUuid(this.userId)
                .nick(this.name)
                .email(this.email)
                .picture(this.picture)
                .build();
    }
}
