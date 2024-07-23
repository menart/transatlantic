package express.atc.backend.mapper;

import express.atc.backend.db.entity.DocumentEntity;
import express.atc.backend.dto.DocumentDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DocumentMapper extends EntityMapper<DocumentDto, DocumentEntity> {
}
