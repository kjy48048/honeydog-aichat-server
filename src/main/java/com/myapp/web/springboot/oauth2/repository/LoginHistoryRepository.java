package com.myapp.web.springboot.oauth2.repository;


import com.myapp.web.springboot.oauth2.domain.LoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *  <pre>
 *      설명: 앱용 로그인 기록 저장 리포지토리
 *      작성자: kimjinyoung
 *      작성일: 2023. 11. 16.
 *  </pre>
 */
public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {
}
