package com.fourstory.fourstory_api.model.tgame.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum MagicOptions implements HasLabel {
    NO_MAGIC(0, "NO_MAGIC"),
    STRENGTH(1, "Strength"),
    SKILL(2, "Skill"),
    ENDURANCE(3, "Endurance"),
    INTELLIGENCE(4, "Intelligence"),
    WISDOM(5, "Wisdom"),
    SPIRIT(6, "Spirit"),
    LIFE(50, "Life"),
    MANA(51, "Mana"),

    ATTACK_RATE(11, "Attack Rate"),
    CRITIC(13, "Critical Hit"),
    ATTACK_SPEED(54, "Attack Speed"),
    LONG_ATTACK_SPEED(55, "Long Range Attack Speed"),

    CONCENTRATION(20, "Concentration"),
    MAGIC_ATTACK_SPEED(56, "Magic Attack Speed"),
    MAGIC_CRITIC(21, "Critical Magic Hit"),
    MAGIC_HIT(86, "Magic Hit Chance"),

    SHIELD_DEFENCE_RATE(34, "Shield Defence Rate"),

    EVADE(12, "Evade"),
    MAGIC_DEFENCE(87, "Magic Defence")
    ;

    private final int magicId;
    private final String label;

    private static final Map<Integer, MagicOptions> MAGIC_ID_MAP = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(MagicOptions::getMagicId, Function.identity()));

    public static MagicOptions getMagicOptions(int magicId) {
        return MAGIC_ID_MAP.get(magicId);
    }
}
