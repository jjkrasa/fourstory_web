package com.fourstory.fourstory_api.dto.response;

import com.fourstory.fourstory_api.model.tgame.enums.EquipSlot;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class CharacterItemResponse {

    private EquipSlot equipSlot;

    private ItemResponse item;

    private Byte upgradedLevel;

    private String effect;

    private Map<String, Short> options;
}
