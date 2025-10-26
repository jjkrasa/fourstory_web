package com.fourstory.fourstory_api.model.tgame;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class GuildMemberId implements Serializable {

    @Column(name = "dwCharID", nullable = false)
    private Integer charId;

    @Column(name = "dwGuildID", nullable = false)
    private Integer guildId;
}
