package com.example.aimeetingtranscription.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private final Jwt jwt = new Jwt();
    private final FileStorage fileStorage = new FileStorage();
    private final Transcription transcription = new Transcription();

    @Getter
    @Setter
    public static class Jwt {
        @NotBlank
        private String secret;
        @Min(60000)
        private long expirationMs = 86400000;
    }

    @Getter
    @Setter
    public static class FileStorage {
        @NotBlank
        private String uploadDir = "uploads";
        @Min(1)
        private long maxFileSizeMb = 100;
    }

    @Getter
    @Setter
    public static class Transcription {
        @NotBlank
        private String apiKey;
        private String model = "gemini-2.5-flash";
        @Min(1)
        private long inlineMaxFileSizeMb = 15;
    }
}
