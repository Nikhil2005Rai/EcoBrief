package com.example.aimeetingtranscription;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AiMeetingTranscriptionApplication {

    public static void main(String[] args) {
        loadDotenvIntoSystemProperties();
        SpringApplication.run(AiMeetingTranscriptionApplication.class, args);
    }

    private static void loadDotenvIntoSystemProperties() {
        Dotenv dotenv = Dotenv.configure()
                .directory(".")
                .filename(".env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        copyIfPresent(dotenv, "GEMINI_API_KEY");
        copyIfPresent(dotenv, "TRANSCRIPTION_API_KEY");
        copyIfPresent(dotenv, "JWT_SECRET");
        copyIfPresent(dotenv, "SPRING_DATASOURCE_URL");
        copyIfPresent(dotenv, "SPRING_DATASOURCE_USERNAME");
        copyIfPresent(dotenv, "SPRING_DATASOURCE_PASSWORD");
    }

    private static void copyIfPresent(Dotenv dotenv, String key) {
        String value = dotenv.get(key);
        String normalized = normalizeEnvValue(value);
        if (normalized != null && !normalized.isBlank() && System.getProperty(key) == null) {
            System.setProperty(key, normalized);
        }
    }

    private static String normalizeEnvValue(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if ((trimmed.startsWith("'") && trimmed.endsWith("'"))
                || (trimmed.startsWith("\"") && trimmed.endsWith("\""))) {
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }
}
