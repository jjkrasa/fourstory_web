package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.response.CharacterItemResponse;
import com.fourstory.fourstory_api.model.tgame.CharacterItem;
import com.fourstory.fourstory_api.service.ItemBonusCalculator;
import lombok.Setter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
        componentModel = "spring",
        uses = { ItemMapper.class, LabelMapper.class },
        unmappedSourcePolicy = ReportingPolicy.WARN,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public abstract class CharacterItemMapper {

    @Setter(onMethod_ =  @Autowired)
    protected ItemBonusCalculator calculator;

    @Mapping(target = "equipSlot", source = "equipSlot")
    @Mapping(target = "item", source = "item")
    @Mapping(target = "upgradedLevel", source = "upgradedLevel")
    @Mapping(target = "effect", source = "gradeEffect", qualifiedByName = "label")
    @Mapping(target = "options", expression = "java(calculator.calculateOptions(characterItem))")
    public abstract CharacterItemResponse characterItemToCharacterItemResponse(CharacterItem characterItem);
}
