package com.myapp.web.springboot.aichatter.repository;

import com.myapp.web.springboot.aichatter.domain.AiChatter;
import com.myapp.web.springboot.aichatter.enums.AccessRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 *  <pre>
 *      설명: aiChatter 저장 리포지토리
 *      작성자: kimjinyoung
 *      작성일: 2024. 03. 06.
 *  </pre>
 */
public interface AiChatterRepository extends JpaRepository<AiChatter, Long> {
    @Query("SELECT ac FROM AiChatter ac WHERE ac.owner.userUuid = :userUuid AND ac.nick = :nick")
    AiChatter findByOwnerUuidAndNick(@Param("userUuid") UUID userUuid, @Param("nick") String nick);

    @Query("SELECT ac FROM AiChatter ac WHERE ac.owner.userUuid = :userUuid")
    List<AiChatter> findListByOwnerUuid(@Param("userUuid") UUID userUuid);

    @Query("SELECT ac FROM AiChatter ac WHERE ac.aiUser.userUuid = :userUuid")
    AiChatter findByAiUserUuid(@Param("userUuid") UUID userUuid);

    @Query("SELECT ac FROM AiChatter ac WHERE ac.owner.userUuid = :userUuid")
    List<AiChatter> findByOwnerUuid(@Param("userUuid") UUID userUuid);

    @Query("SELECT ac FROM AiChatter ac WHERE ac.owner.userUuid = :userUuid AND ac.accessRole = :accessRole")
    List<AiChatter> findAllByOwnerIdAndAccessRole(@Param("userUuid") UUID userUuid, @Param("accessRole") AccessRole accessRole);

    @Query("SELECT ac FROM AiChatter ac WHERE ac.accessRole = :accessRole")
    List<AiChatter> findAllByAccessRole(@Param("accessRole") AccessRole accessRole);
}
