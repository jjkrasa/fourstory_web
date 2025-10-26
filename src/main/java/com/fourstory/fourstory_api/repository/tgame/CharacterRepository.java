package com.fourstory.fourstory_api.repository.tgame;

import com.fourstory.fourstory_api.model.tgame.Character;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;


public interface CharacterRepository extends JpaRepository<Character, Integer> {

    @Query("""
    SELECT DISTINCT c
    FROM GameCharacter c
    LEFT JOIN FETCH c.items i
    LEFT JOIN FETCH i.item it
    LEFT JOIN FETCH c.guildMember gm
    LEFT JOIN FETCH gm.guild g
    JOIN FETCH c.pvpPoint p
    WHERE lower(c.name) = lower(:name)
        AND ((i.ownerType = 0 AND i.storageID = 254 AND i.storageType <> 2) OR i IS NULL)
    """)
    Optional<Character> findByNameWithEquippedItems(@Param("name") String name);

    @Query("""
    SELECT c FROM GameCharacter c
    JOIN FETCH c.pvpPoint p
    WHERE (:name is null OR lower(c.name) LIKE lower(concat('%', :name, '%')))
    ORDER BY p.totalPoint DESC, c.name ASC
    """)
    @EntityGraph(attributePaths = { "guildMember.guild" })
    Page<Character> findByNameOrderByTotalPointDesc(@Param("name") String name, Pageable pageable);
}
