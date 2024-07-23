package express.atc.backend.mapper;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, UserEntity> {
    @Override
    @Mapping(target = "agree", expression = "java(true)")
    UserDto toDto(UserEntity entity);
}
