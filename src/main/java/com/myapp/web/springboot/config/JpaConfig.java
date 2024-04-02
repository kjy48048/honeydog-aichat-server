package com.myapp.web.springboot.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * <pre>
 *     설명: JPA Config
 *     작성자: kimjinyoung
 *     작성일: 2023. 2. 21.
 * </pre>
 */
@Configuration
@EnableJpaAuditing // JPA Auditing 활성화
public class JpaConfig {}
