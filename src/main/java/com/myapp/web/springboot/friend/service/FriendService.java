package com.myapp.web.springboot.friend.service;


import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.repository.AppUserRepository;
import com.myapp.web.springboot.friend.domain.Friend;
import com.myapp.web.springboot.friend.dto.FriendRequestDto;
import com.myapp.web.springboot.friend.dto.FriendResponseDto;
import com.myapp.web.springboot.friend.enums.FriendStatus;
import com.myapp.web.springboot.friend.enums.FriendType;
import com.myapp.web.springboot.friend.repository.FriendRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * <pre>
 *      설명: 앱용 유저 친구관리 관리 서비스
 *      작성자: kimjinyoung
 *      작성일: 2023. 10. 23.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class FriendService {
    private final FriendRepository friendRepository;
    private final AppUserRepository appUserRepository;

    /**
     * 신규 친구 상태 저장
     * 신규로 올 때 가능한 친구상태(REQUESTED, ACCEPTED, BLOCKED, NO_RELATION)
     *
     * @param requestDto 유저 uuid
     * @return 응답 DTO
     */
    @Transactional
    public FriendResponseDto createFriend(FriendRequestDto requestDto) {
        // 생성요청 시나리오...

        // 시나리오1. 내가 친구 요청 함
        // 1-1. 이미 친구관계인지 확인
        // 1-2. 없으면 신규 저장
        // 1-2-1. 내가 친구추가 요청함 상태로 저장(신규)

        // 1-3. 있으면 상태 확인 후 처리 시작
        // 1-3-1. 친구수락되어 있는데 요청한 경우 에러 메세지 출력(종료)
        // 1-3-1. 그외의 관계일 경우 저장(수정)

        // 시나리오2. 내가 친구 차단 요청 함
        // 2-1. 이미 친구관계인지 확인
        // 2-2. 없으면 신규 저장
        // 2-2-1. 내가 친구추가 차단 요청함 상태로 저장(신규)

        // 2-3. 있으면 상태 확인 후 처리 시작
        // 2-3-1 있으면 친구 차단함 상태로 저장

        // 수정요청 시나리오...

        // 시나리오1. 내가 친구 추가 요청 수락함
        // 1-1. 내가 친구추가 수락됨 상태로 저장(수정)
        // 1-2. 친구가 친구추가 수락됨 상태로 저장(수정)

        // 시나리오2. 친구가 거절함
        // 2-1. 친구가 친구추가 무관계 상태로 저장(수정)
        // 2-2. 내가 친구추가 무관계 상태로 저장(수정)

        // 친구이메일이 유저데이터에 존재하는지 확인
        FriendResponseDto responseDto = new FriendResponseDto();
        if(requestDto == null || requestDto.getFriendEmail() == null) {
            log.info("createFriend fail... no found email... > requestDto: {}", requestDto);
            responseDto.setResultCode("FAIL");
            responseDto.setResultMessage("요청이 실패하였습니다.");
            return responseDto;
        }
        AppUser friendUser = appUserRepository.findByEmail(requestDto.getFriendEmail()).orElse(null);

        // 이메일에 해당하는 친구 유저가 없으면 널 리턴
        if (friendUser == null) {
            log.info("createFriend fail... no found friendUser > requestDto: {}", requestDto);
            responseDto.setResultCode("FAIL");
            responseDto.setResultMessage("요청이 실패하였습니다.");
            return responseDto;
        }

        // 친구관계 조회
        Friend friend = friendRepository.findFriendByUserAndFriendUser(UUID.fromString(requestDto.getUserUuid()), friendUser.getUserUuid()).orElse(null);
        FriendStatus friendStatus = FriendStatus.fromString(requestDto.getFriendStatus());

        if(friendStatus == null) {
            log.info("createFriend fail... no found friendUser > requestDto: {}", requestDto);
            responseDto.setResultCode("FAIL");
            responseDto.setResultMessage("요청이 실패하였습니다.");
            return responseDto;
        }

        if (friend == null) {
            // 친구관계 없을 떼(신규)
            AppUser user = appUserRepository.findByUserUuid(UUID.fromString(requestDto.getUserUuid()));

            log.info("frined insert > userUser: {}, friendUser: {}, friendStatus: {}", user.getUserUuid(), friendUser.getUserUuid(), friendStatus);

            // 친구요청 신규 저장...
            friend = Friend.builder()
                    .user(user)
                    .friendUser(friendUser)
                    .friendStatus(friendStatus)
                    .friendType(FriendType.HUMAN)
                    .build();
            friend = friendRepository.save(friend);
        } else {
            // 친구관계 있을 떼(수정)
            FriendStatus savedFriendStatus = friend.getFriendStatus();

            // 이미 친구 수락된 상태일 때 친구 요청한 경우
            if (savedFriendStatus.equals(FriendStatus.ACCEPTED)
                    && friendStatus.equals(FriendStatus.REQUESTED)) {
                // 친구쪽 요청도 수락됨 상태로 변경
                responseDto.setResultMessage("이미 친구추가 되어 있습니다.");
                responseDto.setResultCode("FAIL");
                return responseDto;
            } else {
                // 그외에는 상태 업데이트
                friend.updateStatus(friendStatus);

                log.info("frined update > userUser: {}, friendUser: {}, beforeFriendStatus: {}, afterFriendStatus: {}"
                        , friend.getUser().getUserUuid(), friendUser.getUserUuid(), savedFriendStatus, friendStatus);

                friendRepository.save(friend);
            }
        }

        // 친구관계 요청 처리 중.. 역친구관계 조건에 따른 추가처리...
        if(friendStatus.equals(FriendStatus.ACCEPTED)) {
            // 친구가 나에게 요청 처리하고 내가 수락한 경우 처리...

            // 역친구관계 조회
            Friend reverseFriend = friendRepository
                    .findFriendByUserAndFriendUser(friendUser.getUserUuid()
                            , UUID.fromString(requestDto.getUserUuid()))
                    .orElse(null);

            log.info("friend save additional process... FriendStatus: {}, reverserFriendStatus: {}", friendStatus, reverseFriend != null ? reverseFriend.getFriendStatus() : "");

            // 친구상태 업로드가 수락함 상태일 경우 역친구관계가 차단함 상태가 아닌 경우...
            if(reverseFriend != null
                    && !reverseFriend.getFriendStatus()
                    .equals(FriendStatus.BLOCKED)) {
                FriendStatus savedFriendStatus = reverseFriend.getFriendStatus();

                log.info("frined reverse update > userUser: {}, friendUser: {}, beforeFriendStatus: {}, afterFriendStatus: {}"
                        , friend.getUser().getUserUuid(), friendUser.getUserUuid(), savedFriendStatus, friendStatus);
                reverseFriend.updateStatus(friendStatus);
                friendRepository.save(reverseFriend);
            }
        } else if(friendStatus.equals(FriendStatus.REQUESTED)) {
            // 친구관계가 수락된 상태인데 내가 요청할 경우(내가 친구와 관계 취소나 차단했다가 내가 다시 요청한 경우)

            // 역친구관계 조회
            Friend reverseFriend = friendRepository
                    .findFriendByUserAndFriendUser(friendUser.getUserUuid()
                            , UUID.fromString(requestDto.getUserUuid()))
                    .orElse(null);

            log.info("friend save additional process... FriendStatus: {}, reverserFriendStatus: {}", friendStatus, reverseFriend != null ? reverseFriend.getFriendStatus() : "");

            // 역친구관계가 수락됨 상태일 경우...
            if(reverseFriend != null
                    && reverseFriend.getFriendStatus()
                    .equals(FriendStatus.ACCEPTED)) {
                log.info("frined update > userUser: {}, friendUser: {}, afterFriendStatus: {}"
                        , friend.getUser().getUserUuid(), friendUser.getUserUuid(), friendStatus);
                friend.updateStatus(FriendStatus.ACCEPTED);
                friendRepository.save(friend);
            }
        }

        switch (friendStatus) {
            // 친구요청 신규 저장...
            case REQUESTED -> responseDto.setResultMessage("친구요청이 완료되었습니다.");
            case BLOCKED -> responseDto.setResultMessage("친구 차단요청이 완료되었습니다.");
            case ACCEPTED -> responseDto.setResultMessage("친구요청 수락이 완료되었습니다.");
            case NO_RELATION -> responseDto.setResultMessage("");
        }

        responseDto = new FriendResponseDto(friend);
        responseDto.setResultCode("SUCCESS");

        return responseDto;
    }

    /**
     * 신규 AI 친구상태 저장
     * ACCEPTED , NO_RELATION 만...
     * @param requestDto 유저 uuid
     * @return 응답DTO
     */
    @Transactional
    public FriendResponseDto saveAiFriend(FriendRequestDto requestDto) {
        FriendResponseDto responseDto;

        AppUser user = appUserRepository.findByUserUuid(UUID.fromString(requestDto.getUserUuid()));

        AppUser friendUser = appUserRepository.findByUserUuid(UUID.fromString(requestDto.getAiUserUuid()));

        // 친구관계 조회
        Friend friend = friendRepository.findFriendByUserAndFriendUser(UUID.fromString(requestDto.getUserUuid()), friendUser.getUserUuid()).orElse(null);

        if (friend == null) {
            // 친구관계 없을 떼(신규)
            friend = Friend.builder()
                    .user(user)
                    .friendUser(friendUser)
                    .friendStatus(FriendStatus.fromString(requestDto.getFriendStatus()))
                    .friendType(FriendType.AI)
                    .build();
            friend = friendRepository.save(friend);
        } else {
            // 친구관계 있을 떼(수정)
            friend.updateStatus(FriendStatus.fromString(requestDto.getFriendStatus()));;
        }
        responseDto = new FriendResponseDto(friend);
        responseDto.setResultCode("SUCCESS");
        responseDto.setResultMessage("");

        return responseDto;
    }

    /**
     * 상호친구목록(ACCEPTED) 조회 요청 처리 객체 리턴
     * @param userUuid 유저 uuid
     * @return 응답 리스트
     */
    public List<Friend> findInterFriendListByUser(String userUuid) {
        return friendRepository.findInterFriendListByUser(UUID.fromString(userUuid));
    }

    /**
     * 상호친구목록(ACCEPTED) 조회 요청 처리 Dto 리턴
     * @param userUuid 유저 uuid
     * @return 응답 리스트 Dto
     */
    public List<FriendResponseDto> findInterFriendListDtoByUser(String userUuid) {
        return friendRepository
                .findInterFriendListByUser(UUID.fromString(userUuid))
                .stream()
                .map(FriendResponseDto::new)
                .toList();
    }

    /**
     * 상호친구목록(ACCEPTED) 조회 요청 처리 Dto 리턴
     * @param userUuid 유저 uuid
     * @return 응답 리스트 Dto
     */
    public List<FriendResponseDto> findInterFriendListByUserNotInRoom(String userUuid, String roomUuid) {
        return friendRepository
                .findInterFriendListByUserNotInRoom(UUID.fromString(userUuid), UUID.fromString(roomUuid))
                .stream()
                .map(FriendResponseDto::new)
                .toList();
    }

    /**
     * 친구목록 조회 요청 처리
     * @param requestDto 요청DTO
     * @return 응답 리스트
     */
    public List<FriendResponseDto> findFriendList(FriendRequestDto requestDto) {
        return friendRepository
                .findFriendListByUserAndStatusAndType(
                        UUID.fromString(requestDto.getUserUuid())
                        , FriendStatus.fromString(requestDto.getFriendStatus())
                        , FriendType.fromString(requestDto.getFriendType()))
                .stream()
                .map(FriendResponseDto::new)
                .toList();
    }

    /**
     * 받은 친구목록 조회 요청 처리
     * @param requestDto 요청DTO
     * @return 응답 리스트
     */
    public List<FriendResponseDto> findReverseFriendList(FriendRequestDto requestDto) {
        return friendRepository
                .findReverseFriendListByUserAndStatusAndType(
                        UUID.fromString(requestDto.getUserUuid())
                        , FriendStatus.fromString(requestDto.getFriendStatus())
                        , FriendType.fromString(requestDto.getFriendType()))
                .stream()
                .map(FriendResponseDto::reverseDtoFromDomain)
                .toList();
    }

    /**
     * 유저가 보유한 모든 친구관계 목록 조회
     * @param userUuid 유저id
     * @return 모든 친구관계 목록
     */
    public List<Friend> findAllRelationsShipByUser(String userUuid) {
        return friendRepository.findAllRelationsShipByUser(UUID.fromString(userUuid));
    }

    /**
     * 친구관계 삭제
     * @param friend 친구 엔티티
     */
    public void delete(Friend friend) {
        friendRepository.delete(friend);
    }
}
