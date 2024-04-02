package com.myapp.web.springboot.aichatter.domain;

import com.myapp.web.springboot.aichatter.enums.AccessRole;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: Open Ai Assistants 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@Getter
@NoArgsConstructor
@Entity
public class AiChatter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long aiChatterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AI_UUID", referencedColumnName = "userUuid")
    private AppUser aiUser; // 유저

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OWNER_UUID", referencedColumnName = "userUuid")
    private AppUser owner; // 유저

    @Column(nullable = false)
    private String nick;

    @Column(nullable = false)
    private String encryptToken;

    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessRole accessRole;

    @Builder
    public AiChatter(Long aiChatterId, AppUser aiUser, AppUser owner, String nick, String encryptToken, String model, AccessRole accessRole) {
        this.aiChatterId = aiChatterId;
        this.aiUser = aiUser;
        this.owner = owner;
        this.nick = nick;
        this.encryptToken = encryptToken;
        this.model = model;
        this.accessRole = accessRole;
    }

    public AiChatter updateAccessRole(AccessRole accessRole) {
        this.accessRole = accessRole;
        return this;
    }
}
