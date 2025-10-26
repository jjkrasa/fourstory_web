package com.fourstory.fourstory_api.model.tgame;

import com.fourstory.fourstory_api.model.tgame.enums.EquipSlot;
import com.fourstory.fourstory_api.model.tgame.enums.ItemEffect;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "TITEMTABLE")
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterItem {

    @Id
    @Column(name = "dlID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bStorageType", nullable = false)
    private Byte storageType;

    @Column(name = "dwStorageID")
    private Integer storageID;

    @Column(name = "bOwnerType", nullable = false)
    private Byte ownerType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dwOwnerID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Character character;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "bItemID", nullable = false)
    private EquipSlot equipSlot;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wItemID", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Item item;

    @Column(name = "bLevel", nullable = false)
    private Byte upgradedLevel;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "bGradeEffect", nullable = false)
    private ItemEffect gradeEffect;

    @Column(name = "bMagic1", nullable = false)
    private Byte magic1;

    @Column(name = "bMagic2", nullable = false)
    private Byte magic2;

    @Column(name = "bMagic3", nullable = false)
    private Byte magic3;

    @Column(name = "bMagic4", nullable = false)
    private Byte magic4;

    @Column(name = "bMagic5", nullable = false)
    private Byte magic5;

    @Column(name = "bMagic6", nullable = false)
    private Byte magic6;

    @Column(name = "wValue1", nullable = false)
    private Short value1;

    @Column(name = "wValue2", nullable = false)
    private Short value2;

    @Column(name = "wValue3", nullable = false)
    private Short value3;

    @Column(name = "wValue4", nullable = false)
    private Short value4;

    @Column(name = "wValue5", nullable = false)
    private Short value5;

    @Column(name = "wValue6", nullable = false)
    private Short value6;
}
