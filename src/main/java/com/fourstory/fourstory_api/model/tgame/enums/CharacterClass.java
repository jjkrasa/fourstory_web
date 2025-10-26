package com.fourstory.fourstory_api.model.tgame.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CharacterClass implements HasLabel {
    WARRIOR("Warrior"),
    ASSASSIN("Assassin"),
    ARCHER("Archer"),
    MAGE("Mage"),
    PRIEST("Priest"),
    SUMMONER("Summoner");

    private final String label;
}
