package com.sillador.strecs.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sillador.strecs.entity.Document;
import com.sillador.strecs.repositories.DocumentRepository;
import com.sillador.strecs.repositories.specifications.DocumentSpecification;
import com.sillador.strecs.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {
    private static final Logger logger = LoggerFactory.getLogger(DocumentServiceImpl.class);
    private final DocumentRepository documentRepository;
    private final ObjectMapper objectMapper;

    @Override
    public Document save(Document document) {
        boolean isNew = document.getId() == null;
        Document savedDocument = documentRepository.save(document);

        if (isNew) {
            logger.info("Document created - ID: {}, Title: {}, Category: {}",
                savedDocument.getId(),
                savedDocument.getTitle(),
                savedDocument.getCategory()
            );
        } else {
            try {
                Map<String, Object> changes = new HashMap<>();
                changes.put("title", savedDocument.getTitle());
                changes.put("category", savedDocument.getCategory());
                changes.put("tags", savedDocument.getTags());
                changes.put("publish", savedDocument.isPublish());
                changes.put("draft", savedDocument.isDraft());

                logger.info("Document updated - ID: {}, Changes: {}",
                    savedDocument.getId(),
                    objectMapper.writeValueAsString(changes)
                );
            } catch (Exception e) {
                logger.error("Error logging document update - ID: {}, Error: {}",
                    savedDocument.getId(),
                    e.getMessage()
                );
            }
        }

        return savedDocument;
    }

    @Override
    public Optional<Document> findById(Long id) {
        Optional<Document> document = documentRepository.findById(id);
        if (document.isPresent()) {
            logger.debug("Document retrieved - ID: {}", id);
        } else {
            logger.debug("Document not found - ID: {}", id);
        }
        return document;
    }

    @Override
    public Page<Document> findAll(Map<String, String> params, Pageable pageable) {
        logger.debug("Retrieving documents with params: {}", params);
        return documentRepository.findAll(DocumentSpecification.filter(params), pageable);
    }

    @Override
    public void deleteById(Long id) {
        if (documentRepository.existsById(id)) {
            documentRepository.deleteById(id);
            logger.info("Document deleted - ID: {}", id);
        } else {
            logger.warn("Attempt to delete non-existent document - ID: {}", id);
        }
    }

    @Override
    public boolean existsById(Long id) {
        return documentRepository.existsById(id);
    }
}
