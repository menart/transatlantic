package express.atc.backend.mapper;

import express.atc.backend.db.entity.FileEntity;
import express.atc.backend.dto.FileDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FileMapper extends EntityMapper<FileDto, FileEntity> {
}
