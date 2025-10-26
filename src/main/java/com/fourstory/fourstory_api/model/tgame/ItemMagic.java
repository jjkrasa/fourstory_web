package com.fourstory.fourstory_api.model.tgame;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "TITEMMAGICCHART")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemMagic {

    @Id
    @Column(name = "bMagic", nullable = false)
    private Byte magicId;

    @Column(name = "bRvType", nullable = false)
    private Byte rvType;

    @Column(name = "wMaxValue", nullable = false)
    private Short maxValue;

    @Column(name = "wMaxBound", nullable = false)
    private Short maxBound;

    @Column(name = "wRareBound", nullable = false)
    private Short rareBound;
}
