package com.myapp.web.springboot.appuser.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 웹용 채팅방 관리 Dto
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 05.
 * </pre>
 */
@Data
@NoArgsConstructor
public class WebAppUserRequestDto {
    private String nick;
    private String email;
    private String appUserRole;
    private String appUserStatus;
    private String pageNumber;
    private String pageSize;

    public WebAppUserRequestDto(String nick, String email, String appUserRole, String appUserStatus, String pageNumber, String pageSize) {
        this.nick = nick;
        this.email = email;
        this.appUserRole = appUserRole;
        this.appUserStatus = appUserStatus;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
