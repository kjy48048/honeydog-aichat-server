package com.myapp.web.springboot.common;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
public class GenericSpecifications {
    /**
     * 특정 필드에 대해 like 검색을 수행하는 범용 Specification을 반환합니다.
     *
     * @param fieldName 검색할 필드 이름
     * @param searchTerm 검색어
     * @param <T> 엔티티의 타입
     * @return Specification<T>
     */
    public static <T> Specification<T> like(String fieldName, String searchTerm) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (searchTerm == null || searchTerm.trim().isEmpty()) {
                return cb.isTrue(cb.literal(true)); // 조건 없음
            }
            return cb.like(cb.lower(root.get(fieldName)), "%" + searchTerm.toLowerCase() + "%");
        };
    }

    /**
     * 특정 필드에 대해 equal 검색을 수행하는 범용 Specification을 반환합니다.
     *
     * @param fieldName 검색할 필드 이름
     * @param value 비교할 값
     * @param <T> 엔티티의 타입
     * @return Specification<T>
     */
    public static <T> Specification<T> equal(String fieldName, Object value) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (value == null) {
                return cb.isTrue(cb.literal(true)); // 조건 없음
            }
            return cb.equal(root.get(fieldName), value);
        };
    }
}
