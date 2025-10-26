package com.fourstory.fourstory_api.model.tgame;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "TGUILDMEMBERTABLE")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildMember {

    @EmbeddedId
    private GuildMemberId id;

    @MapsId("charId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dwCharID", nullable = false, unique = true)
    private Character character;

    @MapsId("guildId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dwGuildID", nullable = false)
    private Guild guild;
}
