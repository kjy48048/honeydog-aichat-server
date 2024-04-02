package com.myapp.web.springboot.aichatter.dto;

import com.myapp.web.springboot.aichatter.domain.AiChatter;
import lombok.Data;

/**
 * <pre>
 *     설명: ai 채터 API 요청용 DTO
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@Data
public class AiChatterApiRequestDto {
    private Long aiChatterId;
    private String aiUserUuid;
    private String ownerUuid;
    private String nick;
    private String encryptToken;
    private String model;
    private String AccessRole;

    public AiChatterApiRequestDto(AiChatter aiChatter) {
        this.aiChatterId = aiChatter.getAiChatterId();
        this.aiUserUuid = aiChatter.getAiUser().getUserUuid().toString();
        this.ownerUuid = aiChatter.getOwner().getUserUuid().toString();
        this.nick = aiChatter.getNick();
        this.model = aiChatter.getModel();
        this.encryptToken = aiChatter.getEncryptToken();
    }
}
