package express.atc.backend.mapper;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.dto.LanguageDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, UserEntity> {
    @Override
    @Mapping(target = "agree", expression = "java(true)")
    @Mapping(target = "full", expression = "java(entity.isFullInfo())")
    UserDto toDto(UserEntity entity);
}
