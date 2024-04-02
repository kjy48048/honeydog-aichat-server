package com.myapp.web.springboot.aichatter.service;


import com.myapp.web.springboot.aichatter.domain.AiChatter;
import com.myapp.web.springboot.aichatter.dto.AiChatterApiRequestDto;
import com.myapp.web.springboot.aichatter.dto.AiChatterRequestDto;
import com.myapp.web.springboot.aichatter.dto.AiChatterResponseDto;
import com.myapp.web.springboot.aichatter.enums.AccessRole;
import com.myapp.web.springboot.aichatter.repository.AiChatterRepository;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import com.myapp.web.springboot.appuser.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  <pre>
 *      설명: 앱용 유저 채팅방 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 23.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AiChatterService {
    private final AppUserService appUserService;
    private final AiChatterRepository aiChatterRepository;

    /**
     * AI 저장 요청
     * @param requestDto 요청 저장 DTO
     * @param encryptedToken 암호화된 토큰
     * @return 저장 처리 응답 결과
     */
    @Transactional
    public AiChatterResponseDto createAiUser(AiChatterRequestDto requestDto, String encryptedToken) {
        try {
            AppUser owner = appUserService.findById(requestDto.getUserUuid());

            AppUser aiUser = AppUser.builder()
                    .nick(requestDto.getNick())
                    .picture(requestDto.getPicture())
                    .appUserRole(AppUserRole.AI)
                    .appUserStatus(AppUserStatus.NORMAL)
                    .greetings(requestDto.getGreetings())
                    .build();

            aiUser = appUserService.save(aiUser);

            AiChatter aiChatter = AiChatter.builder()
                    .aiUser(aiUser)
                    .owner(owner)
                    .nick(requestDto.getNick())
                    .encryptToken(encryptedToken)
                    .model(requestDto.getModel())
                    .accessRole(AccessRole.fromString(requestDto.getAccessRole()))
                    .build();

            aiChatter = aiChatterRepository.save(aiChatter);
            return new AiChatterResponseDto(aiUser, aiChatter);
        } catch (Exception e) {
            log.error("createAiUser error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * ai 유저 찾기
     * @param ownerUuid 주인 ID
     * @param nick 닉네임
     * @return ai 유저 응답 dto
     */
    public AiChatterResponseDto findByUserAndNick(String ownerUuid, String nick) {
        try {
            AiChatter aiChatter = aiChatterRepository.findByOwnerUuidAndNick(UUID.fromString(ownerUuid), nick);
            AppUser aiUser = appUserService.findById(aiChatter.getAiUser().getUserUuid().toString());
            return new AiChatterResponseDto(aiUser, aiChatter);
        } catch (Exception e) {
            log.error("findByUserAndNick error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * ai 유저 목록 찾기
     * @param ownerUuid 주인 아이디
     * @return ai 유저 목록
     */
    public List<AiChatterResponseDto> findByUser(String ownerUuid) {
        try {
            List<AiChatter> aiChatters = aiChatterRepository.findByOwnerUuid(UUID.fromString(ownerUuid));
            AppUser aiUser = appUserService.findById(aiChatters.get(0).getAiUser().getUserUuid().toString());
            return aiChatters.stream().map(aiChatter -> new AiChatterResponseDto(aiUser, aiChatter)).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("findByUserAndNick error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * API 호출용 요청 DTO 생성
     * @param aiUserUuid 요청할 AI User uuid
     * @return API 호출 요청 DTO
     */
    public AiChatterApiRequestDto findForApiByAiUser(String aiUserUuid) {
        try {
            AiChatter aiChatter = aiChatterRepository.findByAiUserUuid(UUID.fromString(aiUserUuid));
            return new AiChatterApiRequestDto(aiChatter);
        } catch (Exception e) {
            log.error("findByUserAndNick error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 접근 권한으로 AIChatter 목록 찾기
     * @param accessRole 접근 권한
     * @return AiChatter 목록
     */
    public List<AiChatter> findListByAccessRole(AccessRole accessRole) {
        try {
            return aiChatterRepository.findAllByAccessRole(accessRole);
        } catch (Exception e) {
            log.error("findListByAccessRole error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 접근 권한으로 AIChatter 목록 찾기
     * @param ownerId 소유주 UUID
     * @param accessRole 접근 권한
     * @return AiChatter 목록
     */
    public List<AiChatter> findAllByOwnerIdAndAccessRole(UUID ownerId, AccessRole accessRole) {
        try {
            return aiChatterRepository.findAllByOwnerIdAndAccessRole(ownerId, accessRole);
        } catch (Exception e) {
            log.error("findListByAccessRole error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * ai 유저 수정
     * @param requestDto 요청 dto
     * @return ai 유저 응답 dto
     */
    @Transactional
    public AiChatterResponseDto updateAiUser(AiChatterRequestDto requestDto) {
        try {
            AiChatter aiChatter = aiChatterRepository.findByOwnerUuidAndNick(UUID.fromString(requestDto.getUserUuid()), requestDto.getNick());
            if(StringUtils.hasText(requestDto.getAccessRole())) {
                aiChatter.updateAccessRole(AccessRole.fromString(requestDto.getAccessRole()));
            }
            aiChatter = aiChatterRepository.save(aiChatter);
            AppUser aiUser = appUserService.findById(aiChatter.getAiUser().getUserUuid().toString());

            if(StringUtils.hasText(requestDto.getPicture())) {
                aiUser.updatePicture(requestDto.getPicture());
            }
            if(StringUtils.hasText(requestDto.getGreetings())) {
                aiUser.updateGreetings(requestDto.getGreetings());
            }
            appUserService.saveAppUser(aiUser);
            return new AiChatterResponseDto(aiUser, aiChatter);
        } catch (Exception e) {
            log.error("createAiUser error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * ai 유저 삭제 요청
     * @param ownerUuid 주인 uuid
     * @param nick 닉네임
     * @return ai 유저 삭제 처리결과
     */
    @Transactional
    public Long deleteAiUser(String ownerUuid, String nick) {
        try {
            AiChatter aiChatter = aiChatterRepository.findByOwnerUuidAndNick(UUID.fromString(ownerUuid), nick);
            Long aiChatterId = aiChatter.getAiChatterId();
            aiChatterRepository.delete(aiChatter);

            AppUser aiUser = appUserService.findById(aiChatter.getAiUser().getUserUuid().toString());
            aiUser.updateNick("삭제된 AI"); // 랜덤 닉네임으로 변경
            aiUser.updatePicture(""); // 이미지 없에기
            aiUser.updateAppUserStatus(AppUserStatus.WITHDRAW); // 탈퇴로 변경
            appUserService.saveAppUser(aiUser);
            return aiChatterId;
        } catch (Exception e) {
            log.error("findByUserAndNick error: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 소유하고 있는 모든 AI 삭제
     * @param ownerUuid 소유주 id
     * @return 수유주  id
     */
    @Transactional
    public String deleteAllAiUser(String ownerUuid) {

        List<AiChatter> aiChatters = aiChatterRepository.findListByOwnerUuid(UUID.fromString(ownerUuid));
        for(AiChatter aiChatter : aiChatters) {
            aiChatterRepository.delete(aiChatter);

            AppUser aiUser = appUserService.findById(aiChatter.getAiUser().getUserUuid().toString());
            aiUser.updateNick("삭제된 AI"); // 랜덤 닉네임으로 변경
            aiUser.updatePicture(""); // 이미지 없에기
            aiUser.updateAppUserStatus(AppUserStatus.WITHDRAW); // 탈퇴로 변경
            appUserService.saveAppUser(aiUser);
        }

        return ownerUuid;
    }
}
