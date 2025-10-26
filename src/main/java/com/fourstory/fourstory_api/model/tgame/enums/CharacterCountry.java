package com.fourstory.fourstory_api.model.tgame.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CharacterCountry implements HasLabel {

    VALORIAN("Valorian"),
    DERION("Derion"),
    GOR("Gor");

    private final String label;
}
