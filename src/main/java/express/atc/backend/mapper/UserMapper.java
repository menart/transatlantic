package express.atc.backend.mapper;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.dto.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends EntityMapper<UserDto, UserEntity> {
}
