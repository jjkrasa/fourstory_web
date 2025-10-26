package com.fourstory.fourstory_api.repository.tgame;

import com.fourstory.fourstory_api.dto.projection.CharacterRank;
import com.fourstory.fourstory_api.model.tgame.PvpPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PvpPointRepository extends JpaRepository<PvpPoint, Integer> {

    @Query(value = """
    SELECT COUNT(p) + 1 FROM PvpPoint p WHERE p.totalPoint > (
        SELECT p2.totalPoint FROM PvpPoint p2 WHERE p2.id = :id
        )
    """)
    Long findRankByCharId(@Param("id") int id);

    @Query("""
    SELECT new com.fourstory.fourstory_api.dto.projection.CharacterRank(p1.id,
        (SELECT COUNT(p2) + 1 FROM PvpPoint p2 WHERE p2.totalPoint > p1.totalPoint)
    )
    FROM PvpPoint p1 WHERE p1.id IN :ids
    """)
    List<CharacterRank> findRanksForCharIds(@Param("ids") List<Integer> ids);
}
