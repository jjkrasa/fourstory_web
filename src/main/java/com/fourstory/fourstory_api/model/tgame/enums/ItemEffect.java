package com.fourstory.fourstory_api.model.tgame.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemEffect implements HasLabel {

    NONE(null),
    WATER("Water"),
    LAVA("Lava"),
    LIGHTNING("Lightning"),
    ICE("Ice"),
    DARKNESS("Darkness");

    private final String label;
}
