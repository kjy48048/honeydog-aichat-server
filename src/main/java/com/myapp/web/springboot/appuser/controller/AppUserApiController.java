package com.myapp.web.springboot.appuser.controller;

import com.myapp.web.springboot.appuser.dto.AppUserRequestDto;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.dto.WebAppUserRequestDto;
import com.myapp.web.springboot.appuser.service.AppSystemUserService;
import com.myapp.web.springboot.appuser.service.AppUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     설명: 앱유저 관리 API 컨트롤러
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 20.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/app/user")
@Slf4j
public class AppUserApiController {
    private final AppUserService appUserService;
    private final AppSystemUserService appSystemUserService;

    /**
     * 앱유저 신규 등록
     * @return 유저 UUID
     */
    @PostMapping("")
    public ResponseEntity<String> create(@RequestBody AppUserRequestDto appUserRequestDto) {
        if(appUserRequestDto == null || StringUtils.isNullOrEmpty(appUserRequestDto.getNick())) {
            log.error("ApiUser create request error... Request Nick is Null or Empty!, requestDto: {}", appUserRequestDto);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .header("custom_error_status", "BAD_REQUEST")
                    .header("custom_error_msg", "RequestDto is Null or Empty!")
                    .body("BAD_REQUEST, RequestDto is Null or Empty!");
        }

        if(!StringUtils.isNullOrEmpty(appUserRequestDto.getEmail())) {
            boolean checkHasEmail = appUserService.isDuplicateEmail(appUserRequestDto.getEmail());

            if(checkHasEmail) {
                log.error("ApiUser create request error... Request Email is Duplicated!, requestDto: {}", appUserRequestDto);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .header("custom_error_status", "BAD_REQUEST")
                        .header("custom_error_msg", "Request Email is Duplicated!")
                        .body("BAD_REQUEST, Request Email is Duplicated!");
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.createAppUser(appUserRequestDto));
    }

    /**
     * 앱유저 uuid로 정보 조회
     */
    @GetMapping("")
    public ResponseEntity<AppUserResponseDto> findByUuid(@RequestParam String userUuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.findByUserUuid(userUuid));
    }

    /**
     * 앱유저 닉네임 업데이트
     * @param appUserRequestDto 업데이트할 닉네임/프로필이미지/인사말
     * @return 유저 정보
     */
    @PutMapping("/userUuid/{userUuid}")
    public ResponseEntity<AppUserResponseDto> updateUser(@PathVariable String userUuid, @RequestBody AppUserRequestDto appUserRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.updateUser(userUuid, appUserRequestDto));
    }

    /**
     * ai유저 정보 조회
     */
    @GetMapping("/ai/list/user/{userUuid}")
    public ResponseEntity<List<AppUserResponseDto>> findAllAiUser(@PathVariable String userUuid) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appSystemUserService.findAllAiUserByUserUuid(userUuid));
    }

    /**
     * 본인제외 닉네임으로 유저찾기(친구찾기 기능)
     */
    @GetMapping("/user/nick/like")
    public ResponseEntity<List<AppUserResponseDto>> findUserByNickLikeNotMe(@RequestParam String userUuid, @RequestParam String nick) {
        Pageable pageable = PageRequest.of(0, 5); // 첫 번째 페이지, 페이지 당 5개 항목
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.findUserByNickLikeNotMe(userUuid, nick, pageable));
    }
    /**
     * 본인제외 이메일로 유저찾기(친구찾기 기능)
     */
    @GetMapping("/user/email/like")
    public ResponseEntity<List<AppUserResponseDto>> findUserByEmailLikeNotMe(@RequestParam String userUuid, @RequestParam String email) {
        Pageable pageable = PageRequest.of(0, 5); // 첫 번째 페이지, 페이지 당 5개 항목
        return ResponseEntity.status(HttpStatus.CREATED).body(appUserService.findUserByEmailLikeNotMe(userUuid, email, pageable));
    }

    /**
     * 웹에서 관리용 유저 조회
     * @param nick 유저명
     * @param email 이메일
     * @param appUserRole 유저권한
     * @param appUserStatus 유저상태
     * @param pageNumber 페이지번호
     * @param pageSize 페이지크기
     * @return 응답DTO
     */
    @GetMapping("/list/search")
    public ResponseEntity<List<AppUserResponseDto>> findSearchUser(
            @RequestParam(required = false) String nick,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String appUserRole,
            @RequestParam(required = false) String appUserStatus,
            @RequestParam(defaultValue = "0") String pageNumber,
            @RequestParam(defaultValue = "10") String pageSize) {
        WebAppUserRequestDto requestDto = new WebAppUserRequestDto(nick, email, appUserRole, appUserStatus, pageNumber, pageSize);
        log.info("=== findSearchRoom ===");
        List<AppUserResponseDto> responseDtoList = appUserService.findByWebUserRequest(requestDto);
        return responseDtoList == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    @DeleteMapping("/withdraw/{userUuid}")
    public ResponseEntity<AppUserResponseDto> withdrawUser(@PathVariable String userUuid) {
        log.info("=== withdrawUser ===");
        AppUserResponseDto responseDto = appSystemUserService.withdrawUser(userUuid);
        return responseDto == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
