package com.myapp.web.springboot.posts.repository;

import com.myapp.web.springboot.posts.domain.Posts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Query("SELECT p FROM Posts p ORDER BY p.postsId DESC")
    List<Posts> findAllDesc();
}
