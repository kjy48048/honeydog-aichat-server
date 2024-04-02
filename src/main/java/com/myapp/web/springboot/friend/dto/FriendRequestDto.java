package com.myapp.web.springboot.friend.dto;

import lombok.Data;

/**
 * <pre>
 *     설명: 앱용 유저 친구Dto
 *     작성자: 김진영
 *     작성일: 2024. 02. 01.
 * </pre>
 */
@Data
public class FriendRequestDto {
    String userUuid;
    String aiUserUuid;
    String friendEmail;
    String friendStatus;
    String friendType;
}
