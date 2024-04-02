package com.myapp.web.springboot.approom.dto;

import lombok.Data;

import java.util.List;

/**
 * <pre>
 *     설명: 웹용 채팅방 관리 Dto
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 06.
 * </pre>
 */
@Data
public class WebRoomListResponse {
    private List<AppRoomResponseDto> roomList;
    private int totalPages;
    private int pageNumber;
}
