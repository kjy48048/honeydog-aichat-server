package com.myapp.web.springboot.oauth2.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import com.myapp.web.springboot.config.auth.dto.OAuthAttributes;
import com.myapp.web.springboot.oauth2.enums.LoginHistoryStatus;
import com.myapp.web.springboot.oauth2.enums.LoginResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 *  <pre>
 *      설명: 앱용 OAuth2 인증서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 15.
 *  </pre>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserService {
    private final AppUserRepository appUserRepository;
    private final RestTemplate restTemplate;

    @Transactional
    public AppUserResponseDto findOrSaveMember(String idToken, String provider) throws ParseException, JsonProcessingException {
        OAuthAttributes oAuth2Attribute;
        switch (provider) {
            case "google":
                oAuth2Attribute = getGoogleData(idToken);
                break;
            default:
                throw new RuntimeException("제공하지 않는 인증기관입니다.");
        }

        String email = oAuth2Attribute.toAppUserEntity().getEmail();

        AppUser findMember = appUserRepository.findByEmail(email)
                .orElseGet(() -> {
                    AppUser appUser = oAuth2Attribute.toAppUserEntity();
                    return appUserRepository.save(appUser);
                });

        return new AppUserResponseDto(findMember);
    }

    /**
     * 구글 인증 테스트...
     * @param idToken idToken
     * @return 테스트 결과...
     */
    public Map<String, Object> getGoogleDataTest(String idToken) {
        log.info("=== getGoogleDataTest start ===");
        Map<String, Object> testMap = new HashMap<>();
        try {
            OAuthAttributes oAuthAttributes = this.getGoogleData(idToken);
            AppUser appUser = oAuthAttributes.toAppUserEntity();
            AppUserResponseDto responseDto = new AppUserResponseDto(appUser);

            testMap.put("code", LoginResponseCode.SUCCESS);
            testMap.put("oAuthAttributes", oAuthAttributes);
            testMap.put("appUser", appUser);
            testMap.put("appUserResponseDto", responseDto);

        } catch (Exception e) {
            testMap.put("code", LoginResponseCode.ERROR_INVALID_REQUEST);
            testMap.put("error_description", e.getMessage());
            log.error("getGoogleDataTest error...", e);
        }
        for(String key : testMap.keySet()) {
            log.info("[key: {}, value: {}]", key, testMap.get(key));
        }
        log.info("=== getGoogleDataTest end ===");
        return testMap;
    }
    private OAuthAttributes getGoogleData(String idToken)  throws ParseException, JsonProcessingException {

        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String googleApi = "https://oauth2.googleapis.com/tokeninfo";
        String targetUrl = UriComponentsBuilder.fromHttpUrl(googleApi).queryParam("id_token", idToken).build().toUriString();

        Map<String, Object> body;
        ResponseEntity<String> response;
        try {
            response = restTemplate.exchange(targetUrl, HttpMethod.GET, entity, String.class);

            JSONParser parser = new JSONParser();
            JSONObject jsonBody = (JSONObject) parser.parse(response.getBody());

            body = new ObjectMapper().readValue(jsonBody.toString(), Map.class);


            log.info("=== getGoogleData body ===");
            for(String key : body.keySet()) {
                log.info("[key: {}, value: {}]", key, body.get(key).toString());
            }
            log.info("=== getGoogleData body ===");

        } catch (Exception e) {
            log.error("getGoogleData error... idToken: {}, e: {}", idToken, e.getMessage(), e);
            throw e;
        }

        return OAuthAttributes.of("google", "sub", body);
    }
}
