package express.atc.backend.mapper;

import express.atc.backend.db.entity.DocumentEntity;
import express.atc.backend.dto.DocumentDto;
import express.atc.backend.enums.DocumentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface DocumentMapper extends EntityMapper<DocumentDto, DocumentEntity> {
    @Mapping(target="type", source = "typeId", qualifiedByName = "docIdToDocument")
    DocumentEntity toEntity(DocumentDto dto);

    @Mapping(target = "typeId", source = "type", qualifiedByName = "documentToDocId")
    @Mapping(target = "type", source = "type", qualifiedByName = "documentToDocName")
    DocumentDto toDto(DocumentEntity entity);

    @Named("docIdToDocument")
    default DocumentType docIdToDocument(Integer docId){
        return DocumentType.getDocumentTypeById(docId);
    }

    @Named("documentToDocId")
    default Integer documentToDocId(DocumentType type){
        return type.getId();
    }

    @Named("documentToDocName")
    default String documentToDocName(DocumentType type){
        return type.getRus();
    }
}
