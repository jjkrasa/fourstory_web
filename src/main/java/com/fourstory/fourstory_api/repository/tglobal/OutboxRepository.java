package com.fourstory.fourstory_api.repository.tglobal;

import com.fourstory.fourstory_api.model.tglobal.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, Long> {

    @Query(value = """
        SELECT TOP (:limit) * FROM outbox WITH (READPAST, ROWLOCK, UPDLOCK)
        WHERE processedAt IS NULL
        ORDER BY createdAt
        """, nativeQuery = true)
    List<Outbox> fetchBatch(@Param("limit") int limit);
}
