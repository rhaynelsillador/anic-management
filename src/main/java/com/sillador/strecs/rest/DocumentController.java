package com.sillador.strecs.rest;

import com.sillador.strecs.entity.Document;
import com.sillador.strecs.services.DocumentService;
import com.sillador.strecs.utility.BaseResponse;
import com.sillador.strecs.utility.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {


    private final DocumentService documentService;

    @PostMapping
    public BaseResponse create(@RequestBody Document document) {
        return new BaseResponse().build(documentService.save(document));
    }

    @GetMapping("/{id}")
    public BaseResponse findById(@PathVariable Long id) {
        return documentService.findById(id)
                .map(document -> new BaseResponse().build(document))
                .orElse(new BaseResponse().build(ResponseCode.NOT_FOUND, "Document not found"));
    }

    @GetMapping
    public BaseResponse findAll(@RequestParam Map<String, String> params, Pageable pageable) {
        Page<Document> documents = documentService.findAll(params, pageable);
        BaseResponse response = new BaseResponse().build(documents.getContent());
        response.setPage(new com.sillador.strecs.utility.Page(
                documents.getTotalElements(), documents.getSize()
        ));
        return response;
    }

    @PutMapping("/{id}")
    public BaseResponse update(@PathVariable Long id, @RequestBody Document document) {
        if (!documentService.existsById(id)) {
            return new BaseResponse().build(ResponseCode.NOT_FOUND, "Document not found");
        }
        document.setId(id);
        return new BaseResponse().build(documentService.save(document));
    }

    @DeleteMapping("/{id}")
    public BaseResponse delete(@PathVariable Long id) {
        if (!documentService.existsById(id)) {
            return new BaseResponse().build(ResponseCode.NOT_FOUND, "Document not found");
        }
        documentService.deleteById(id);
        return new BaseResponse().success();
    }
}
