package express.atc.backend.mapper;

import express.atc.backend.dto.UserDto;
import express.atc.backend.security.UserDetail;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserDetailMapper {

    UserDetail toUserDetail(UserDto dto);
}
