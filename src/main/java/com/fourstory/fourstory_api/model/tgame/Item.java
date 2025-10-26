package com.fourstory.fourstory_api.model.tgame;


import jakarta.persistence.*;
import lombok.*;

@Table(name = "TITEMCHART")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @Column(name = "wItemID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "szNAME", nullable = false, length = 100)
    private String name;

    @Column(name = "fRevision", nullable = false)
    private Float physicRevision;

    @Column(name = "fMRevision", nullable = false)
    private Float magicRevision;

    @Column(name = "fAtRate", nullable = false)
    private Float atRate;

    @Column(name = "fMAtRate", nullable = false)
    private Float matRat;

    public float revisionByRvType(byte rvType) {
        return switch (rvType) {
            case 1 -> physicRevision;
            case 2 -> magicRevision;
            case 3 -> atRate;
            case 4 -> matRat;
            default -> 1.0f;
        };
    }
}
