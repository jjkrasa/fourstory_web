package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.model.tgame.enums.HasLabel;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface LabelMapper {

    @Named("label")
    default String label(HasLabel label) {
        return label == null ? null : label.getLabel();
    }
}
