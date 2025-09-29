package com.fourstory.fourstory_api.repository.tglobal;

import com.fourstory.fourstory_api.model.tglobal.VerificationToken;
import com.fourstory.fourstory_api.model.tglobal.VerificationTokenType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {

    @EntityGraph(attributePaths = "user")
    @Query("""
        SELECT vt FROM VerificationToken vt WHERE vt.tokenHash = :hash
        AND vt.tokenType = :tokenType
        AND vt.consumedAt IS NULL
        AND vt.expiresAt > :now
        """)
    Optional<VerificationToken> findActive(@Param("hash") byte[] hash,
                                           @Param("tokenType") VerificationTokenType tokenType,
                                           @Param("now") Instant now);

    @Modifying
    @Query("""
        UPDATE VerificationToken vt SET vt.consumedAt = :now
        WHERE vt.user.id = :userId
        AND vt.tokenType = :tokenType
        AND vt.consumedAt IS NULL
        """)
    int invalidateActive(@Param("userId") Integer userId,
                         @Param("tokenType") VerificationTokenType tokenType,
                         @Param("now") Instant now);
}
