package com.fourstory.fourstory_api.mapper;

import com.fourstory.fourstory_api.dto.request.RegisterRequest;
import com.fourstory.fourstory_api.model.tglobal.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.WARN,
        unmappedTargetPolicy = ReportingPolicy.WARN
)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "email", source = "email")
    @Mapping(target = "registrationEmail", source = "email")
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "role", expression = "java(com.fourstory.fourstory_api.model.tglobal.Role.USER)")
    @Mapping(target = "checkFlag", constant = "0")
    @Mapping(target = "firstLogin", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    User registerRequestToUser(RegisterRequest request);
}
