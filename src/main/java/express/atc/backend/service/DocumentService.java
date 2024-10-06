package express.atc.backend.service;

import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.dto.DocumentDto;

public interface DocumentService {
    DocumentDto addOrUpdateDocument(DocumentDto document);

    DocumentDto findDocumentForUser(UserEntity user);
}
