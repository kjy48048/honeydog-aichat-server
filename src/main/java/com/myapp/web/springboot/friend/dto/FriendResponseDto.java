package com.myapp.web.springboot.friend.dto;

import com.myapp.web.springboot.friend.domain.Friend;
import com.myapp.web.springboot.friend.enums.FriendStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 *     설명: 앱용 친구 응답Dto
 *     작성자: 김진영
 *     작성일: 2024. 02. 01.
 * </pre>
 */
@Data
public class FriendResponseDto {
    private String resultCode;
    private String resultMessage;

    private String nick;
    private String email;
    private String picture;
    private String greetings;

    private String friendStatus;
    private String friendType;
    private LocalDateTime modifiedDate;

    public String getFormattedModifiedDate() {
        return modifiedDate != null ? modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) : "";
    }

    public FriendResponseDto() {

    }

    public FriendResponseDto(Friend friend) {
        if(friend == null) return;

        if(friend.getFriendUser() != null) {
            this.nick = friend.getFriendUser().getNick();
            this.email = friend.getFriendUser().getEmail();
            this.picture = friend.getFriendUser().getPicture();
        }
        this.friendStatus = friend.getFriendStatus() != null ? friend.getFriendStatus().name() : "";
        this.friendType = friend.getFriendType() != null ? friend.getFriendType().name() : "";
        this.modifiedDate = friend.getModifiedDate();
    }

    public static FriendResponseDto reverseDtoFromDomain(Friend friend) {
        if(friend == null) return null;

        FriendResponseDto reverseResponseDto = new FriendResponseDto();

        if(friend.getFriendUser() != null) {
            reverseResponseDto.setNick(friend.getUser().getNick());
            reverseResponseDto.setEmail(friend.getUser().getEmail());
            reverseResponseDto.setPicture(friend.getUser().getPicture());
            reverseResponseDto.setGreetings(friend.getUser().getGreetings());
        }
        reverseResponseDto.setFriendStatus(friend.getFriendStatus() != null ? friend.getFriendStatus().name() : "");
        reverseResponseDto.setFriendType(friend.getFriendType() != null ? friend.getFriendType().name() : "");
        reverseResponseDto.setModifiedDate(friend.getModifiedDate());

        return reverseResponseDto;
    }
}
