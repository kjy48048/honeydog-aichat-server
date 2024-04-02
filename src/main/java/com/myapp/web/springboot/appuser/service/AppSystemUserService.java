package com.myapp.web.springboot.appuser.service;


import com.myapp.web.springboot.aichatter.domain.AiChatter;
import com.myapp.web.springboot.aichatter.enums.AccessRole;
import com.myapp.web.springboot.aichatter.service.AiChatterService;
import com.myapp.web.springboot.appuser.domain.AppUser;
import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomService;
import com.myapp.web.springboot.friend.domain.Friend;
import com.myapp.web.springboot.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 *  <pre>
 *      설명: 앱용 유저 시스템 관리 서비스
 *      ai 친구관련 작업 위해 추가
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 09.
 *  </pre>
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class AppSystemUserService {
    private final AppUserService appUserService;
    private final FriendService friendService;
    private final AiChatterService aiChatterService;
    private final AppUserRoomService appUserRoomService;

    /**
     * 앱 AI유저 목록용 AI 유저 리스트 가져오기
     * 1. AiChatter데이터에 PUBLIC으로 된 것 모두 가져오기
     * 2. 상호친구된 목록 가져오기 -> 친구목록에서 가져온 UserUuid로 친구가 소유한 AiChatter 중 FRIEND 가져오기
     * 3. 내 UserUuid로 등록된(FRIEND, PRIVATE) AiChatter 모두 가져오기
     * 4. 매번 그렇게 가져오기 힘드니까 특정한 상황에서만 그렇게 호출하도록...
     * @return AI 유저 목록
     */
    public List<AppUserResponseDto> findAllAiUserByUserUuid(String userUuid) {

        List<UUID> aiUserList;

        // 1. PUBLIC 목록
        List<AiChatter> aiChatters = aiChatterService.findListByAccessRole(AccessRole.PUBLIC);


        // 2. FRIEND 목록
        List<Friend> findInterFriendList = friendService.findInterFriendListByUser(userUuid);
        List<AppUser> friendUserList = new ArrayList<>();
        if(findInterFriendList != null && findInterFriendList.size() > 0) {
            friendUserList = findInterFriendList
                    .stream()
                    .map(Friend::getFriendUser)
                    .toList();
        }
        for(AppUser friendUser : friendUserList) {
            List<AiChatter> friendsFriendAiChatter = aiChatterService.findAllByOwnerIdAndAccessRole(friendUser.getUserUuid(), AccessRole.FRIEND);
            List<AiChatter> friendsPrivateAiChatter = aiChatterService.findAllByOwnerIdAndAccessRole(friendUser.getUserUuid(), AccessRole.PRIVATE);

            aiChatters.addAll(friendsFriendAiChatter);
            aiChatters.addAll(friendsPrivateAiChatter);
        }


        // 3. 내 AI 목록
        List<AiChatter> myFriendAiChatter = aiChatterService.findAllByOwnerIdAndAccessRole(UUID.fromString(userUuid), AccessRole.FRIEND);
        List<AiChatter> myPrivateAiChatter = aiChatterService.findAllByOwnerIdAndAccessRole(UUID.fromString(userUuid), AccessRole.PRIVATE);

        // 합치기
        aiChatters.addAll(myFriendAiChatter);
        aiChatters.addAll(myPrivateAiChatter);

        //
        aiUserList = aiChatters
                .stream()
                .map(AiChatter::getAiUser)
                .map(AppUser::getUserUuid)
                .collect(Collectors.toList());

        List<AppUserResponseDto> responseDtoList = new ArrayList<>();

        aiUserList.forEach(aiUser -> responseDtoList.add(appUserService.findByUserUuid(aiUser.toString())));

        return responseDtoList;
    }

    /**
     * 탈퇴
     * @param userUuid 유저 uuid
     * @return 탈퇴 처리 결과
     */
    @Transactional
    public AppUserResponseDto withdrawUser(String userUuid) {

        AppUser appUser = appUserService.findById(userUuid);
        if(appUser != null) {
            // 모든 친구관계 삭제
            friendService.findAllRelationsShipByUser(userUuid).forEach(friendService::delete);

            // 모든 방 찾아서 나간상태로 바꾸기
            appUserRoomService.findJoinRoomsByUser(userUuid).forEach(appUserRoom -> appUserRoomService.save(appUserRoom.outRoom()));

            // 등록한 ai 모든 삭제
            aiChatterService.deleteAllAiUser(userUuid);

            // 유저 탈퇴 처리
            appUser.withdrawUser();
            appUserService.save(appUser);
            return new AppUserResponseDto(appUser);
        } else {
            return null;
        }
    }
}
