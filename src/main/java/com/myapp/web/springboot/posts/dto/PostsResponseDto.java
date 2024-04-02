package com.myapp.web.springboot.posts.dto;

import com.myapp.web.springboot.posts.domain.Posts;
import lombok.Getter;

@Getter
public class PostsResponseDto {
    private Long postsId;
    private String title;
    private String content;
    private String author;

    public PostsResponseDto(Posts entity) {
        this.postsId = entity.getPostsId();
        this.title = entity.getTitle();
        this.content = entity.getContent();
        this.author = entity.getAuthor();
    }
}
