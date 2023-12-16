package com.epam.mapper;

import com.epam.model.User;
import com.epam.model.dto.UserWithPassword;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "password", source = "encodedPassword")
    User toEntity(UserWithPassword userWithPassword);
}
