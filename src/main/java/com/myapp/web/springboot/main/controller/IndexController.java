package com.myapp.web.springboot.main.controller;

import com.myapp.web.springboot.appuser.enums.AppUserRole;
import com.myapp.web.springboot.config.auth.LoginUser;
import com.myapp.web.springboot.config.auth.dto.SessionUser;
import com.myapp.web.springboot.posts.dto.PostsResponseDto;
import com.myapp.web.springboot.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
@Slf4j
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postsService.findAllDesc());

        if(user != null) {
            model.addAttribute("userName", user.getName());
            log.info("로그인유저 체크 || user: {}", user);
            // 슈퍼관리자 체크
            if(user.getRole() != null && AppUserRole.ADMIN.name().equals(user.getRole())) {
                model.addAttribute("isAdmin", true);
            }
        }

        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave() {
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("posts", dto);
        return "posts-update";
    }
}
