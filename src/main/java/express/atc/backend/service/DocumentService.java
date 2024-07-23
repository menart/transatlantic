package express.atc.backend.service;

import express.atc.backend.dto.DocumentDto;

public interface DocumentService {
    DocumentDto addOrUpdateDocument(DocumentDto document);
}
