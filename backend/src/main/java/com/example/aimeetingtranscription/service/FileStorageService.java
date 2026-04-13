package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.config.AppProperties;
import com.example.aimeetingtranscription.exception.AppException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private static final Map<String, String> ALLOWED_EXTENSIONS = Map.ofEntries(
            Map.entry("mp3", "audio/mpeg"),
            Map.entry("wav", "audio/wav"),
            Map.entry("flac", "audio/flac"),
            Map.entry("aac", "audio/aac"),
            Map.entry("ogg", "audio/ogg"),
            Map.entry("oga", "audio/ogg"),
            Map.entry("webm", "audio/webm"),
            Map.entry("m4a", "audio/mp4"),
            Map.entry("mp4", "audio/mp4")
    );

    private final AppProperties appProperties;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Path.of(appProperties.getFileStorage().getUploadDir()));
        } catch (IOException ex) {
            throw new AppException("Unable to initialize upload directory");
        }
    }

    public String store(MultipartFile file) {
        validate(file);
        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + extension.toLowerCase(Locale.ROOT);
        Path targetPath = Path.of(appProperties.getFileStorage().getUploadDir(), fileName);

        try {
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new AppException("Failed to store uploaded file");
        }

        return targetPath.toString();
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppException("Audio file is required");
        }

        long maxSizeBytes = appProperties.getFileStorage().getMaxFileSizeMb() * 1024 * 1024;
        if (file.getSize() > maxSizeBytes) {
            throw new AppException("File exceeds maximum allowed size of "
                    + appProperties.getFileStorage().getMaxFileSizeMb() + " MB");
        }

        String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
        if (extension == null || !ALLOWED_EXTENSIONS.containsKey(extension.toLowerCase(Locale.ROOT))) {
            throw new AppException("Supported audio formats are mp3, wav, flac, aac, ogg, webm, m4a, and mp4");
        }
    }
}
