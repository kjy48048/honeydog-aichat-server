package com.myapp.web.springboot.approom.controller;

import com.myapp.web.springboot.approom.dto.*;
import com.myapp.web.springboot.approom.service.AppRoomService;
import com.myapp.web.springboot.approom.service.AppRoomSystemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     설명: 앱용 채팅방 API
 *     작성자: kimjinyoung
 *     작성일: 2023. 10. 23.
 * </pre>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v2/app/room")
@Slf4j
public class AppRoomApiController {
    private final AppRoomService appRoomService;
    private final AppRoomSystemService appRoomSystemService;

    /**
     * 채팅방 생성 요청
     * @param requestDto 채팅방 생성 요청 Dto
     * @return 채팅방 uuId
     */
    @PostMapping("")
    public ResponseEntity<AppRoomResponseDto> enterRoom(@RequestBody AppRoomRequestDto requestDto) {
        log.info("=== enterRoom ===");
        log.info("=== requestDto: {}", requestDto);
        AppRoomResponseDto responseDto = appRoomSystemService.enterRoom(requestDto);
        if(responseDto != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 채팅방 닉변경 요청
     *
     * @param requestDto 요청 Dto
     * @return 채팅방 uuId
     */
    @PutMapping("/update-nick")
    public ResponseEntity<AppRoomResponseDto> updateNick(@RequestBody AppRoomRequestDto requestDto) {
        log.info("=== updateNick ==");
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(appRoomService.updateRoomNick(requestDto));
        } catch (Exception e) {
            log.error("room save error... requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 1) 채팅방 나가기
     * 2) 채팅방에 모든 인원이 다 나갔을 경우 닫힘 요청
     *
     * @param roomUuid 채팅방 UUID
     * @return 채팅방 uuId
     */
    @PutMapping("/out-room/room-id/{roomUuid}")
    public ResponseEntity<AppRoomResponseDto> outRoom(@PathVariable String roomUuid) {
        log.info("=== outRoom ==");
        AppRoomResponseDto responseDto = appRoomSystemService.outRoom(roomUuid);
        if(responseDto != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 채팅방 조회 요청
     *
     * @param roomUuid 채팅방 UUID
     * @return 채팅방 응답 Dto
     */
    @GetMapping("/room-id")
    public ResponseEntity<AppRoomResponseDto> findRoomByRoomUuId(@RequestParam String roomUuid) {
        log.info("=== findRoomByRoomUuId ==");
        AppRoomResponseDto responseDto = appRoomService.findRoom(roomUuid);
        return responseDto == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    /**
     * 웹에서 관리용 채팅방 조회
     * @param roomNick 방제
     * @param roomStatus 방상태
     * @param roomType 방타입
     * @param pageNumber 페이지번호
     * @param pageSize 페이지크기
     * @return 응답DTO
     */
    @GetMapping("/list/search")
    public ResponseEntity<WebRoomListResponse> findSearchRoom(
            @RequestParam(required = false) String roomNick,
            @RequestParam(required = false) String roomStatus,
            @RequestParam(required = false) String roomType,
            @RequestParam(defaultValue = "0") String pageNumber,
            @RequestParam(defaultValue = "10") String pageSize) {
        WebRoomRequestDto requestDto = new WebRoomRequestDto(roomNick, roomStatus, roomType, pageNumber, pageSize);
        log.info("=== findSearchRoom ===");
        WebRoomListResponse responseDtoList = appRoomService.findByWebRoomRequest(requestDto);
        return responseDtoList == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDtoList);
    }

    /**
     * 채팅방 목록 조회 요청
     *
     * @param userUuid 유저 UUID
     * @return 채팅방 응답 Dto 리스트
     */
    @GetMapping("/list/user-id")
    public ResponseEntity<List<AppRoomListResponseDto>> findJoinRooms(@RequestParam String userUuid) {
        log.info("=== findJoinRooms ==");
        List<AppRoomListResponseDto> responseDto = appRoomSystemService.findJoinRooms(userUuid);
        if(responseDto != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
