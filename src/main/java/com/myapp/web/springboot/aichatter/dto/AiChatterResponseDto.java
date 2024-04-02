package com.myapp.web.springboot.aichatter.dto;


import com.myapp.web.springboot.aichatter.domain.AiChatter;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.dto.CommonResponseDto;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <pre>
 *     설명: ai 채터 응답용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AiChatterResponseDto extends CommonResponseDto {
    private Long aiChatterId;
    private String userUuid;
    private String nick;
    private String picture;
    private String accessRole;
    private String greetings;

    public AiChatterResponseDto(AppUser appUser, AiChatter aiChatter) {
        this.aiChatterId = aiChatter.getAiChatterId();
        this.nick = aiChatter.getNick();
        this.accessRole = aiChatter.getAccessRole().name();
        this.userUuid = appUser.getUserUuid().toString();
        this.picture = appUser.getPicture();
        this.greetings = appUser.getGreetings();
    }

    public AiChatterResponseDto() {

    }
}
