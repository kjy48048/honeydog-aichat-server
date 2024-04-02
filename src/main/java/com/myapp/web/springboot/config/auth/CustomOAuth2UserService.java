package com.myapp.web.springboot.config.auth;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import com.myapp.web.springboot.config.auth.dto.OAuthAttributes;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * <pre>
 *     설명: OAuth 유저 인증 서비스
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final AppUserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User>
                delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest
                .getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        AppUser user = this.saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(
                new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.attributes(),
                attributes.nameAttributeKey());
    }

    private AppUser saveOrUpdate(OAuthAttributes attributes) {
        AppUser user = userRepository.findByEmail(attributes.email())
                .map(entity -> entity.update(attributes.name(), attributes.picture()))
                .orElse(attributes.toAppUserEntity());

        return userRepository.save(user);
    }
}
