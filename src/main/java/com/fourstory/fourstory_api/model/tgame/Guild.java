package com.fourstory.fourstory_api.model.tgame;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Table(name = "TGUILDTABLE")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Guild {

    @Id
    @Column(name = "dwID", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "szName", nullable = false, length = 50, unique = true)
    private String name;

    @OneToMany(mappedBy = "guild", fetch = FetchType.LAZY)
    private Set<GuildMember> characters;
}
