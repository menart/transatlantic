package express.atc.backend.service.Impl;

import express.atc.backend.db.repository.DocumentRepository;
import express.atc.backend.dto.DocumentDto;
import express.atc.backend.mapper.DocumentMapper;
import express.atc.backend.service.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Override
    public DocumentDto addOrUpdateDocument(DocumentDto document) {
        var entity = documentRepository.findByUser(document.getUser());
        entity = documentMapper.toEntity(document)
                .setId(Objects.nonNull(entity) ? entity.getId() : null);
        return documentMapper.toDto(documentRepository.save(entity));
    }
}
