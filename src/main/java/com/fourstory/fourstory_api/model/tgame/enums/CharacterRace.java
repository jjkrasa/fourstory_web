package com.fourstory.fourstory_api.model.tgame.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CharacterRace implements HasLabel {
    HUMAN("Human"),
    FELINE("Feline"),
    FAIRY("Fairy");

    private final String label;
}
