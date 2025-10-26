package com.fourstory.fourstory_api.model.tgame;

import com.fourstory.fourstory_api.model.tgame.enums.CharacterClass;
import com.fourstory.fourstory_api.model.tgame.enums.CharacterCountry;
import com.fourstory.fourstory_api.model.tgame.enums.CharacterRace;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Table(name = "TCHARTABLE")
@Entity(name = "GameCharacter")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Character {

    @Id
    @Column(name = "dwCharID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "szNAME", nullable = false, unique = true, length = 50)
    private String name;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "bClass", nullable = false)
    private CharacterClass characterClass;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "bRace", nullable = false)
    private CharacterRace race;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "bCountry", nullable = false)
    private CharacterCountry country;

    @Column(name = "bSex", nullable = false)
    private Byte sex;

    @Column(name = "bLevel", nullable = false)
    private Byte level;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private List<CharacterItem> items;

    @OneToMany(mappedBy = "character", fetch = FetchType.LAZY)
    private Set<GuildMember> guildMember;

    @OneToOne(mappedBy = "character", fetch = FetchType.LAZY, optional = false)
    private PvpPoint pvpPoint;

    @Transient
    public String getGuildName() {
        if (guildMember == null || guildMember.isEmpty()) return null;

        return guildMember
                .stream()
                .findFirst()
                .map(GuildMember::getGuild)
                .map(Guild::getName)
                .orElse(null);
    }
}