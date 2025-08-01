package com.sillador.strecs.services;

import com.sillador.strecs.entity.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface DocumentService {
    Document save(Document document);
    Optional<Document> findById(Long id);
    Page<Document> findAll(Map<String, String> params, Pageable pageable);
    void deleteById(Long id);
    boolean existsById(Long id);
}
