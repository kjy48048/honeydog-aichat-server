package com.myapp.web.springboot.main.controller;

import com.myapp.web.springboot.approom.dto.AppRoomResponseDto;
import com.myapp.web.springboot.approom.service.AppRoomService;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuserroom.dto.AppUserRoomResponseDto;
import com.myapp.web.springboot.appuserroom.enums.UserRoomRole;
import com.myapp.web.springboot.appuserroom.service.AppUserRoomService;
import com.myapp.web.springboot.config.auth.LoginUser;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

/**
 * <pre>
 *     설명: 채팅방 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 03. 05.
 * </pre>
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class LobbyController {
    private final AppRoomService roomService;
    private final AppUserRoomService userRoomService;

    /**
     * 채팅로비
     * @param model 모델
     * @param user 유저
     * @return 채팅로비 화면
     */
    @GetMapping("/lobby")
    public String lobby(Model model, @LoginUser SessionUser user) {

        if(user != null) {
            model.addAttribute("userName", user.getName());
            model.addAttribute("userId", user.getUserId());
        }

        return "lobby";
    }

    /**
     * 채팅방
     * @param model 모델
     * @param user 유저
     * @param roomUuid 채팅방 아이디
     * @return 채팅방 화면
     */
    @GetMapping("/room/{roomUuid}")
    public String room(Model model, @LoginUser SessionUser user, @PathVariable String roomUuid) {

        AppRoomResponseDto room = roomService.findRoom(roomUuid);

        if(user == null || room == null) {
            return "redirect:/";
        }

        AppUserRoomResponseDto roomUser = userRoomService.findOrSave(user.toEntity().getUserUuid(), UUID.fromString(roomUuid));

        if(UserRoomRole.HOST.equals(roomUser.getUserRoomRole())) {
            model.addAttribute("isHost", true);
        }

        model.addAttribute("room", room);
        model.addAttribute("roomUser", roomUser);
        model.addAttribute("userUuid", roomUser.getAppUserUuid());
        model.addAttribute("roomUuid", roomUuid);

        model.addAttribute("userName", user.getName());
        model.addAttribute("sender", user.getName());

        return "room";
    }

    /**
     * 채팅방 개설
     * @param user 유저
     * @return 채팅방 개설화면
     */
    @GetMapping("/room/save")
    public String roomSave(Model model, @LoginUser SessionUser user) {

        //todo 유저 롤 체크... 게스트면 못하게...
        if(user == null) return "redirect:/";
        if(user.getRole() != null && AppUserRole.ADMIN.equals(AppUserRole.fromString(user.getRole()))) return "redirect:/";

        model.addAttribute("userName", user.getName());
        return "room-save";
    }

}
