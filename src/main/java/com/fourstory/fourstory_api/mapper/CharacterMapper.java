package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.response.CharacterDetailResponse;
import com.fourstory.fourstory_api.dto.response.CharacterResponse;
import com.fourstory.fourstory_api.model.tgame.Character;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = { CharacterItemMapper.class, LabelMapper.class },
        unmappedSourcePolicy = ReportingPolicy.WARN,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface CharacterMapper {

    @Mapping(target = "name", source = "character.name")
    @Mapping(target = "level", source = "character.level")
    @Mapping(target = "strClass", source = "character.characterClass", qualifiedByName = "label")
    @Mapping(target = "country", source = "character.country", qualifiedByName = "label")
    @Mapping(target = "guild", source = "character.guildName")
    @Mapping(target = "honor", source = "character.pvpPoint.totalPoint")
    @Mapping(target = "rank", source = "rank")
    CharacterResponse characterToCharacterResponse(Character character, Long rank);

    @Mapping(target = "name", source = "character.name")
    @Mapping(target = "level", source = "character.level")
    @Mapping(target = "strClass", source = "character.characterClass", qualifiedByName = "label")
    @Mapping(target = "country", source = "character.country", qualifiedByName = "label")
    @Mapping(target = "guild", source = "character.guildName")
    @Mapping(target = "honor", source = "character.pvpPoint.totalPoint")
    @Mapping(target = "items", source = "character.items")
    @Mapping(target = "rank", source = "rank")
    CharacterDetailResponse characterToCharacterDetailResponse(Character character, Long rank);
}
