package com.myapp.web.springboot.config.auth.dto;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import lombok.Builder;

import java.util.Map;

/**
 * <pre>
 *     설명: OAuth 속성
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
public record OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
    @Builder
    public OAuthAttributes {
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("naver".equals(registrationId)) {
            return ofNaver("id", attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuthAttributes.builder()
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("picture"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName,
                                           Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public AppUser toAppUserEntity() {
        return AppUser.builder()
                .nick(name)
                .email(email)
                .picture(picture)
                .appUserRole(AppUserRole.USER)
                .appUserStatus(AppUserStatus.NORMAL)
                .build();
    }
}
