package com.myapp.web.springboot.main.controller;

import com.myapp.web.springboot.appuser.dto.AppUserResponseDto;
import com.myapp.web.springboot.appuser.dto.WebAppUserRequestDto;
import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.appuser.service.AppUserService;
import com.myapp.web.springboot.config.auth.LoginUser;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * <pre>
 *     설명: 어드민 유저 관리 처리
 *     작성자: kimjinyoung
 *     작성일: 2024. 3. 05.
 * </pre>
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class UserBoardController {
    private final AppUserService appUserService;
    /**
     * 유저 관리 게시판
     * @param model 모델
     * @param user 세션유저
     * @return 유저 관리 게시판
     */
    @GetMapping("/user-board")
    public String userBoard(Model model, @LoginUser SessionUser user) {

        if(user.getRole() == null || !AppUserRole.ADMIN.name().equals(user.getRole())) {
            return "redirect:/";
        }

        List<AppUserResponseDto> users = appUserService.findByWebUserRequest(new WebAppUserRequestDto());
        model.addAttribute("users", users);

        return "user-board";
    }

}
