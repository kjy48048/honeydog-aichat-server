package com.myapp.web.springboot.recommend.repository;

import com.myapp.web.springboot.recommend.domain.Recommend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendRepository extends JpaRepository<Recommend, Long> {
}
