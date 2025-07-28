package express.atc.backend.integration.cfapi.mapper;

import express.atc.backend.dto.UserDto;
import express.atc.backend.integration.cfapi.dto.PersonInfoDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserInfoMapper {

    @Mapping(target = "name", source = "firstName")
    @Mapping(target = "patronymic", source = "lastName")
    @Mapping(target = "lastName", source = "surname")
    @Mapping(target = "birthDate", source = "birthday")
    @Mapping(target = "docTypeCode", source = "document.typeId")
    @Mapping(target = "docSeries", source = "document.series")
    @Mapping(target = "docNumber", source = "document.number")
    @Mapping(target = "docDate", source = "document.issueDate")
    @Mapping(target = "docOrganization", source = "document.nameDepartment")
    @Mapping(target = "docOrganizationCode", source = "document.idDepartment")
    @Mapping(target = "taxNumber", source = "inn")
    PersonInfoDto toPersonalInfoForCfApi(UserDto dto);
}
