package com.fourstory.fourstory_api.model.tgame;

import jakarta.persistence.*;
import lombok.*;

@Table(name = "TPVPOINTTABLE")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PvpPoint {

    @Id
    @Column(name = "dwCharID", nullable = false)
    private Integer id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dwCharID", nullable = false)
    private Character character;

    @Column(name = "dwTotalPoint", nullable = false)
    private Integer totalPoint;
}
