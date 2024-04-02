package com.myapp.web.springboot.friend.controller;


import com.myapp.web.springboot.friend.dto.FriendRequestDto;
import com.myapp.web.springboot.friend.dto.FriendResponseDto;
import com.myapp.web.springboot.friend.service.FriendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     설명: 친구관련 API
 *     작성자: kimjinyoung
 *     작성일: 2024. 02. 01.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/app/friend")
@Slf4j
public class FriendApiController {
    private final FriendService friendService;

    /**
     * 상호친구목록(ACCEPTED) 조회
     * @param userUuid 유저 UUID
     * @return 채팅목록
     */
    @GetMapping("/inter-list")
    public ResponseEntity<List<FriendResponseDto>> findInterFriendListByUser(@RequestParam String userUuid) {
        try {
            log.info("=== findInterFriendListByUser ===");
            log.info("findInterFriendListByUser > userUuid: {}", userUuid);
            List<FriendResponseDto> friendList = friendService.findInterFriendListDtoByUser(userUuid);

            log.info("findInterFriendListByUser > friendList size: {}", friendList != null ? friendList.size() : 0);
            return ResponseEntity.status(HttpStatus.CREATED).body(friendList);
        } catch (Exception e) {
            log.error("findInterFriendListByUser error... userUuid: {}", userUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 방에 없는 상호친구목록(ACCEPTED) 조회
     * @param userUuid 유저 UUID
     * @param roomUuid 방 UUID
     * @return 채팅목록
     */
    @GetMapping("/inter-list/not-in-room")
    public ResponseEntity<List<FriendResponseDto>> findInterFriendListByUserNotInRoom(@RequestParam String userUuid, @RequestParam String roomUuid) {
        try {
            log.info("=== findInterFriendListByUserNotInRoom ===");
            log.info("findInterFriendListByUserNotInRoom > userUuid: {}. roomUuid: {}", userUuid, roomUuid);
            List<FriendResponseDto> friendList = friendService.findInterFriendListByUserNotInRoom(userUuid, roomUuid);

            log.info("findInterFriendListByUserNotInRoom > friendList size: {}", friendList != null ? friendList.size() : 0);
            return ResponseEntity.status(HttpStatus.CREATED).body(friendList);
        } catch (Exception e) {
            log.error("findInterFriendListByUserNotInRoom error... userUuid: {}", userUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 친구목록 조회
     * @param userUuid 유저 UUID
     * @param friendStatus 친구상태(요청, 수락, 거절...)
     * @param friendType 친구타입(AI/사람)
     * @return 채팅목록
     */
    @GetMapping("/list")
    public ResponseEntity<List<FriendResponseDto>> findFriendList(@RequestParam String userUuid, @RequestParam String friendStatus, @RequestParam String friendType) {
        try {
            log.info("=== findFriendList ===");
            log.info("findFriendList > userUuid: {}, friendStatus: {}, friendType: {}", userUuid, friendStatus, friendType);
            FriendRequestDto requestDto = new FriendRequestDto();
            requestDto.setUserUuid(userUuid);
            requestDto.setFriendStatus(friendStatus);
            requestDto.setFriendType(friendType);
            List<FriendResponseDto> friendList = friendService.findFriendList(requestDto);

            log.info("findFriendList > friendList size: {}", friendList != null ? friendList.size() : 0);
            return ResponseEntity.status(HttpStatus.CREATED).body(friendList);
        } catch (Exception e) {
            log.error("findFriendList error... userUuid: {}", userUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 받은 친구목록 조회
     * @param userUuid 유저 UUID
     * @param friendStatus 친구상태(요청, 수락, 거절...)
     * @param friendType 친구타입(AI/사람)
     * @return 채팅목록
     */
    @GetMapping("/reverse-list")
    public ResponseEntity<List<FriendResponseDto>> findReverseFriendList(@RequestParam String userUuid, @RequestParam String friendStatus, @RequestParam String friendType) {
        try {
            log.info("=== findReverseFriendList ===");
            FriendRequestDto requestDto = new FriendRequestDto();
            requestDto.setUserUuid(userUuid);
            requestDto.setFriendStatus(friendStatus);
            requestDto.setFriendType(friendType);
            List<FriendResponseDto> friendList = friendService.findReverseFriendList(requestDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(friendList);
        } catch (Exception e) {
            log.error("findReverseFriendList error... userUuid: {}", userUuid, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * 친구관계 생성/수정 요청
     * 상태는 REQUESTED, ACCEPTED, BLOCKED, NO_RELATION...
     * @param requestDto 친구관계 생성 요청 Dto
     * @return 친구관계 응답 dto
     */
    @PostMapping("")
    public ResponseEntity<FriendResponseDto> save(@RequestBody FriendRequestDto requestDto) {
        log.info("=== save ==");
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(friendService.createFriend(requestDto));
        } catch (Exception e) {
            log.error("friend save error... requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    /**
     * AI 친구관계 생성 요청
     * 상태는 ACCEPTED ...
     * @param requestDto 친구관계 생성 요청 Dto
     * @return 친구관계 응답 dto
     */
    @PostMapping("/ai")
    public ResponseEntity<FriendResponseDto> aiSave(@RequestBody FriendRequestDto requestDto) {
        log.info("=== ai save ==");
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(friendService.saveAiFriend(requestDto));
        } catch (Exception e) {
            log.error("friend save error... requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
