package com.twelvenexus.oneplan.identity.mapper;

import com.twelvenexus.oneplan.identity.dto.UserDto;
import com.twelvenexus.oneplan.identity.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "status", expression = "java(user.getStatus().name())")
    UserDto userToUserDto(User user);

    @Mapping(target = "passwordHash", ignore = true)
    void updateUserFromDto(UserDto userDto, @MappingTarget User user);
}
