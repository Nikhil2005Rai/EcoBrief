package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.config.AppProperties;
import com.example.aimeetingtranscription.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private static final String GEMINI_FILE_UPLOAD_URL =
            "https://generativelanguage.googleapis.com/upload/v1beta/files?key={apiKey}";
    private static final String GEMINI_GENERATE_CONTENT_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/{model}:generateContent?key={apiKey}";

    private final RestClient restClient;
    private final AppProperties appProperties;
    private final ObjectMapper objectMapper;

    public String transcribe(String filePath) {
        try {
            Path path = Path.of(filePath);
            String mimeType = resolveMimeType(path);
            UploadedFile uploadedFile = uploadFile(path, mimeType);
            return generateTranscript(uploadedFile);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Transcription request failed", ex);
            throw new AppException("Failed to transcribe audio file");
        }
    }

    private UploadedFile uploadFile(Path path, String mimeType) throws IOException {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(path) {
            @Override
            public String getFilename() {
                return path.getFileName().toString();
            }
        });

        try {
            Map<?, ?> response = restClient.post()
                    .uri(GEMINI_FILE_UPLOAD_URL, appProperties.getTranscription().getApiKey())
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .body(body)
                    .retrieve()
                    .body(Map.class);

            Object fileObj = response == null ? null : response.get("file");
            if (!(fileObj instanceof Map<?, ?> fileMap)) {
                throw new AppException("Gemini file upload failed: missing file metadata");
            }

            Object uriObj = fileMap.get("uri");
            if (uriObj == null) {
                throw new AppException("Gemini file upload failed: missing file URI");
            }

            Object mimeTypeObj = fileMap.get("mimeType");
            String uploadedMimeType = mimeTypeObj == null ? mimeType : mimeTypeObj.toString();
            return new UploadedFile(uriObj.toString(), uploadedMimeType);
        } catch (HttpStatusCodeException ex) {
            throw new AppException("Gemini file upload failed: " + extractErrorMessage(ex.getResponseBodyAsString()));
        }
    }

    private String generateTranscript(UploadedFile uploadedFile) {
        Map<String, Object> payload = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of("file_data", Map.of(
                                                "mime_type", uploadedFile.mimeType(),
                                                "file_uri", uploadedFile.uri()
                                        )),
                                        Map.of("text", "Transcribe this audio clearly.")
                                )
                        )
                ),
                "generationConfig", Map.of("temperature", 0.2)
        );

        try {
            Map<?, ?> response = restClient.post()
                    .uri(GEMINI_GENERATE_CONTENT_URL,
                            appProperties.getTranscription().getModel(),
                            appProperties.getTranscription().getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            String transcript = extractTranscript(response);
            if (transcript == null || transcript.isBlank()) {
                String errorMessage = extractGeminiError(response);
                throw new AppException(errorMessage == null
                        ? "Gemini transcription failed: empty response"
                        : "Gemini transcription failed: " + errorMessage);
            }
            return transcript;
        } catch (HttpStatusCodeException ex) {
            throw new AppException("Gemini transcription failed: " + extractErrorMessage(ex.getResponseBodyAsString()));
        }
    }

    private String extractTranscript(Map<?, ?> response) {
        if (response == null) {
            return null;
        }

        Object candidatesObj = response.get("candidates");
        if (!(candidatesObj instanceof List<?> candidates) || candidates.isEmpty()) {
            return null;
        }

        Object candidateObj = candidates.get(0);
        if (!(candidateObj instanceof Map<?, ?> candidateMap)) {
            return null;
        }

        Object contentObj = candidateMap.get("content");
        if (!(contentObj instanceof Map<?, ?> contentMap)) {
            return null;
        }

        Object partsObj = contentMap.get("parts");
        if (!(partsObj instanceof List<?> parts) || parts.isEmpty()) {
            return null;
        }

        StringBuilder transcript = new StringBuilder();
        for (Object part : parts) {
            if (part instanceof Map<?, ?> partMap && partMap.get("text") != null) {
                if (!transcript.isEmpty()) {
                    transcript.append(System.lineSeparator()).append(System.lineSeparator());
                }
                transcript.append(partMap.get("text"));
            }
        }

        return transcript.isEmpty() ? null : transcript.toString();
    }

    private String extractGeminiError(Map<?, ?> response) {
        if (response == null) {
            return null;
        }
        Object errorObj = response.get("error");
        if (!(errorObj instanceof Map<?, ?> errorMap)) {
            return null;
        }
        Object messageObj = errorMap.get("message");
        return messageObj == null ? null : messageObj.toString();
    }

    private String extractErrorMessage(String responseBody) {
        try {
            Map<?, ?> response = responseBody == null || responseBody.isBlank()
                    ? Map.of()
                    : objectMapper.readValue(responseBody, Map.class);
            String message = extractGeminiError(response);
            return message == null ? responseBody : message;
        } catch (Exception ignored) {
            return responseBody == null || responseBody.isBlank() ? "Unknown Gemini error" : responseBody;
        }
    }

    private String resolveMimeType(Path path) throws IOException {
        String mimeType = Files.probeContentType(path);
        if (mimeType != null && !mimeType.isBlank()) {
            return mimeType;
        }

        String fileName = path.getFileName().toString().toLowerCase();
        if (fileName.endsWith(".wav")) {
            return "audio/wav";
        }
        if (fileName.endsWith(".flac")) {
            return "audio/flac";
        }
        if (fileName.endsWith(".aac")) {
            return "audio/aac";
        }
        if (fileName.endsWith(".ogg") || fileName.endsWith(".oga")) {
            return "audio/ogg";
        }
        if (fileName.endsWith(".webm")) {
            return "audio/webm";
        }
        if (fileName.endsWith(".m4a") || fileName.endsWith(".mp4")) {
            return "audio/mp4";
        }
        return "audio/mpeg";
    }

    private record UploadedFile(String uri, String mimeType) {
    }
}
