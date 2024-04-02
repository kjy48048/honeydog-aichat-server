package com.myapp.web.springboot.appuser.dto;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import lombok.Data;

import java.util.UUID;

/**
 * <pre>
 *     설명: 앱용 유저 응답Dto
 *     작성자: 김진영
 *     작성일: 2023. 10. 20.
 * </pre>
 */
@Data
public class AppUserRequestDto {
    private String userUuid;
    private String nick;
    private String email;
    private String picture;
    private String greetings;
    private String appUserRole;

    public AppUser toEntity() {
        return AppUser.builder()
                .userUuid(UUID.fromString(userUuid))
                .nick(nick)
                .email(email)
                .picture(picture)
                .greetings(greetings)
                .appUserRole(AppUserRole.fromString(appUserRole))
                .appUserStatus(AppUserStatus.NORMAL)
                .build();
    }
}
