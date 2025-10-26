package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.response.ItemResponse;
import com.fourstory.fourstory_api.model.tgame.Item;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.WARN,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ItemMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ItemResponse itemToItemResponse(Item item);
}
