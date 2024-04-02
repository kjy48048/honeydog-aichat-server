package com.myapp.web.springboot.appuser.service;

import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserRequestDto;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.dto.WebAppUserRequestDto;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.enums.AppUserStatus;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import com.myapp.web.springboot.appuserroom.domain.AppUserRoom;
import com.myapp.web.springboot.common.GenericSpecifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  <pre>
 *      설명: 앱용 유저 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 20.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppUserService {
    private final AppUserRepository appUserRepository;
    private AppUserResponseDto systemUserDto = null;

    /**
     * 시스템 유저 가져오기...
     * @return 시스템 유저
     */
    public AppUserResponseDto findOrSaveSystemUser() {
        if(systemUserDto != null) return systemUserDto;

        AppUser systemUser = appUserRepository.findSystemUser().orElse(null);

        if(systemUser == null) {
            systemUser = AppUser.builder()
                    .nick(AppUserRole.SYSTEM.name())
                    .appUserRole(AppUserRole.SYSTEM)
                    .appUserStatus(AppUserStatus.NORMAL)
                    .build();
            appUserRepository.save(systemUser);
        }

        return systemUserDto = new AppUserResponseDto(systemUser);
    }

    public AppUserResponseDto findByUserUuid(String userUuid) {
        return new AppUserResponseDto(appUserRepository.findByUserUuid(UUID.fromString(userUuid)));
    }

    public AppUser findById(String userUuid) {
        return appUserRepository.findById(UUID.fromString(userUuid)).orElse(null);
    }

    /**
     * 신규 앱유저 저장
     * @return 유저 uuid
     */
    @Transactional
    public String createAppUser(AppUserRequestDto requestDto) {
        AppUser appUser = AppUser.builder()
                .nick(requestDto.getNick())
                .email(requestDto.getEmail())
                .picture(requestDto.getPicture())
                .appUserRole(AppUserRole.USER)
                .appUserStatus(AppUserStatus.NORMAL)
                .build();
        appUser = appUserRepository.save(appUser);
        return appUser.getUserUuid().toString();
    }

    /**
     * 유저 정보 저장...
     * @param appUser 저장요청 엔티티
     * @return 유저 UUID
     */
    @Transactional
    public AppUserResponseDto saveAppUser(AppUser appUser) {
        return new AppUserResponseDto(this.save(appUser));
    }

    /**
     * 유저 정보 저장...
     * @param appUser 저장요청 엔티티
     * @return 유저 UUID
     */
    @Transactional
    public AppUser save(AppUser appUser) {
        return appUserRepository.save(appUser);
    }

    /**
     * 닉네임 업데이트
     * @param userUuid 유저 UUID
     * @param appUserRequestDto 유저Dto
     * @return 유저 UUID
     */
    @Transactional
    public AppUserResponseDto updateUser(String userUuid, AppUserRequestDto appUserRequestDto) {
        AppUser appUser = appUserRepository.findByUserUuid(UUID.fromString(userUuid));
        boolean isUpdate = false;
        if(appUserRequestDto != null) {
            if(appUserRequestDto.getPicture() != null) {
                appUser.updatePicture(appUserRequestDto.getPicture());
                isUpdate = true;
            } else if(appUserRequestDto.getNick() != null) {
                appUser.updateNick(appUserRequestDto.getNick());
                isUpdate = true;
            } else if(appUserRequestDto.getGreetings() != null) {
                appUser.updateGreetings(appUserRequestDto.getGreetings());
                isUpdate = true;
            }
        }
        if(isUpdate) {
            log.info("updateUser > requestDto: {}", appUserRequestDto);
            appUser = appUserRepository.save(appUser);
        }
        return new AppUserResponseDto(appUser);
    }

    public AppUser findByEmail(String email) {
        return appUserRepository.findByEmail(email).orElse(null);
    }

    /**
     * 닉네임 중복여부 체크
     * @param email 이메일
     * @return true(중복), false(중복X)
     */
    public boolean isDuplicateEmail(String email) {
        if(!StringUtils.isNullOrEmpty(email)) {
            return appUserRepository.existsByEmail(email);
        }
        return false;
    }

    /**
     * 닉네임으로 본인 제외 유저 찾기
     * @param userUuid 본인UUID
     * @param nick 닉네임
     * @return
     */
    public List<AppUserResponseDto> findUserByNickLikeNotMe(String userUuid, String nick, Pageable pageable) {
        return appUserRepository.findUserByNickLikeNotMe(UUID.fromString(userUuid), nick, pageable)
                .stream().map(AppUserResponseDto::new).toList();
    }

    /**
     * 이메일로 본인 제외 유저 찾기
     * @param userUuid 본인UUID
     * @param email 이메일
     * @return
     */
    public List<AppUserResponseDto> findUserByEmailLikeNotMe(String userUuid, String email, Pageable pageable) {
        return appUserRepository.findUserByEmailLikeNotMe(UUID.fromString(userUuid), email, pageable)
                .stream().map(AppUserResponseDto::new).toList();
    }

    /**
     * 관리자용 웹에서 유저 목록 조회
     * @param requestDto 요청 DTO
     * @return 검색된 유저 목록
     */
    public List<AppUserResponseDto> findByWebUserRequest(WebAppUserRequestDto requestDto) {
        int pageNumber;
        int pageSize;
        try {
            pageNumber = StringUtils.isNumber(requestDto.getPageNumber()) ? Integer.parseInt(requestDto.getPageNumber()) : 0;
            pageSize = StringUtils.isNumber(requestDto.getPageSize()) ? Integer.parseInt(requestDto.getPageSize()) : 10;
        } catch (Exception e) {
            log.error("findByWebRoomRequest number format error... e: {}", e.getMessage(), e);
            pageNumber = 0;
            pageSize = 10;
        }

        Specification<AppUser> spec = Specification.where(null);

        if(!StringUtils.isNullOrEmpty(requestDto.getNick())) {
            spec.and(GenericSpecifications.like("nick", requestDto.getNick()));

        }

        if(!StringUtils.isNullOrEmpty(requestDto.getEmail())) {
            spec.and(GenericSpecifications.like("email", requestDto.getEmail()));
        }

        if(!StringUtils.isNullOrEmpty(requestDto.getAppUserRole())) {
            spec.and(GenericSpecifications.equal("appUserRole", requestDto.getAppUserRole()));
        }

        if(!StringUtils.isNullOrEmpty(requestDto.getAppUserStatus())) {
            spec.and(GenericSpecifications.equal("appUserStatus", requestDto.getAppUserStatus()));
        }

        return appUserRepository.findAll(spec, PageRequest.of(pageNumber,pageSize, Sort.by(Sort.Direction.DESC, "modifiedDate")))
                .stream()
                .map(AppUserResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 방 아이디로 ai 유저 찾기
     * @param roomUuid 방 아이디
     * @return aiUser
     */
    public AppUser findAiUserByRoomUuid(UUID roomUuid) {
        List<AppUser> aiUsersInRoom = appUserRepository.findAiUsersByRoomUuid(roomUuid);
        return aiUsersInRoom != null && aiUsersInRoom.size() > 0 ? aiUsersInRoom.get(0) : null;
    }
}
