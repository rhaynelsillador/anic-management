package com.sillador.strecs.services;

import java.io.IOException;
import java.util.Map;

public interface TranscriptService {
    
    byte[] generateTranscriptPdf(Long studentId, Map<String, Object> templateData) throws IOException;
}
