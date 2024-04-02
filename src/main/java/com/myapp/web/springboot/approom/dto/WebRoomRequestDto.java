package com.myapp.web.springboot.approom.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <pre>
 *     설명: 웹용 채팅방 관리 Dto
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 04.
 * </pre>
 */
@Data
@NoArgsConstructor
public class WebRoomRequestDto {
    private String roomNick;
    private String roomStatus;
    private String roomType;
    private String pageNumber;
    private String pageSize;

    public WebRoomRequestDto(String roomNick, String roomStatus, String roomType, String pageNumber, String pageSize) {
        this.roomNick = roomNick;
        this.roomStatus = roomStatus;
        this.roomType = roomType;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }
}
