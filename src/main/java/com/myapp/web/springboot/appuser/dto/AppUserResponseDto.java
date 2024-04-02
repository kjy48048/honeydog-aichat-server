package com.myapp.web.springboot.appuser.dto;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 *     설명: 앱용 유저 응답Dto
 *     작성자: 김진영
 *     작성일: 2023. 10. 20.
 * </pre>
 */
@Data
public class AppUserResponseDto {
    private String userUuid;
    private String nick;
    private String email;
    private String greetings;
    private String picture;
    private String appUserRole;
    private String appUserStatus;
    private LocalDateTime modifiedDate;

    public String getFormattedModifiedDate() {
        return modifiedDate != null ? modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public AppUserResponseDto(AppUser appUser) {
        if(appUser == null) return;
        this.userUuid = appUser.getUserUuid() != null ? appUser.getUserUuid().toString() : "";
        this.nick = appUser.getNick();
        this.email = appUser.getEmail();
        this.picture = appUser.getPicture();
        this.appUserRole = appUser.getAppUserRole() != null ? appUser.getAppUserRole().name() : "";
        this.greetings = StringUtils.hasText(appUser.getGreetings()) ? appUser.getGreetings() : "안녕하세요. 만나서 반갑습니다.";
        this.appUserStatus = appUser.getAppUserStatus() != null ? appUser.getAppUserStatus().name() : AppUserStatus.ABNORMAL.name();
        this.modifiedDate = appUser.getModifiedDate();
    }
}
