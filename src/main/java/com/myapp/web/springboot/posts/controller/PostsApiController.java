package com.myapp.web.springboot.posts.controller;

import com.myapp.web.springboot.posts.dto.PostsResponseDto;
import com.myapp.web.springboot.posts.dto.PostsSaveRequestDto;
import com.myapp.web.springboot.posts.dto.PostsUpdateRequestDto;
import com.myapp.web.springboot.posts.service.PostsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class PostsApiController {

    private final PostsService postsService;

    @PostMapping("/api/v1/posts")
    public ResponseEntity<Long> save(@RequestBody PostsSaveRequestDto requestDto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(postsService.save(requestDto));
        } catch (Exception e) {
            log.error("posts save error, requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/api/v1/posts/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(postsService.update(id, requestDto));
        } catch (Exception e) {
            log.error("posts update error, requestDto: {}", requestDto, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @GetMapping("/api/v1/posts/{id}")
    public ResponseEntity<PostsResponseDto> findById(@PathVariable Long id) {
        PostsResponseDto responseDto = postsService.findById(id);
        return responseDto == null
                ? ResponseEntity.status(HttpStatus.NOT_FOUND).build()
                : ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        try {
            postsService.delete(id);
            return ResponseEntity.status(HttpStatus.OK).body(id);
        } catch (Exception e) {
            log.error("posts delete error, id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
