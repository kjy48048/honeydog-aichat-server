package com.myapp.web.springboot.main.controller;

import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.config.auth.LoginUser;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import com.myapp.web.springboot.recommend.dto.RecommendWebResponseDto;
import com.myapp.web.springboot.recommend.service.RecommendService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <pre>
 *     설명: 추천질문 관리
 *     작성자: kimjinyoung
 *     작성일: 2024. 04. 01.
 * </pre>
 */
@RequiredArgsConstructor
@Controller
@Slf4j
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping("/recommend")
    public String recommend(Model model, @LoginUser SessionUser user) {
        model.addAttribute("recommends", recommendService.findAll());

        if(user != null) {
            model.addAttribute("userName", user.getName());
            log.info("로그인유저 체크 || user: {}", user);
            // 슈퍼관리자 체크
            if(user.getRole() != null && AppUserRole.ADMIN.name().equals(user.getRole())) {
                model.addAttribute("isAdmin", true);
            }
        }

        return "recommend";
    }

    @GetMapping("/recommend/save")
    public String recommendSave() {
        return "recommend-save";
    }

    @GetMapping("/recommend/update/{id}")
    public String recommendUpdate(@PathVariable Long id, Model model) {
        RecommendWebResponseDto dto = recommendService.findById(id);
        model.addAttribute("recommend", dto);
        return "recommend-update";
    }
}
