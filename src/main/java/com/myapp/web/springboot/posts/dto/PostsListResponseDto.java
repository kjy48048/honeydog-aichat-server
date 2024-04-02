package com.myapp.web.springboot.posts.dto;

import com.myapp.web.springboot.posts.domain.Posts;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
public class PostsListResponseDto {
    private Long postsId;
    private String title;
    private String author;
    private LocalDateTime modifiedDate;

    public String getFormattedModifiedDate() {
        return modifiedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public PostsListResponseDto(Posts entity) {
        this.postsId = entity.getPostsId();
        this.title = entity.getTitle();
        this.author = entity.getAuthor();
        this.modifiedDate = entity.getModifiedDate();
    }
}
