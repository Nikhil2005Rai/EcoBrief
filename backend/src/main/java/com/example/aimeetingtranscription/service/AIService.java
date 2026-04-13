package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.dto.meeting.MeetingSummaryDto;
import com.example.aimeetingtranscription.exception.AppException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AIService {

    private final ChatClient.Builder chatClientBuilder;
    private final ObjectMapper objectMapper;

    public MeetingSummaryDto summarizeTranscript(String transcript) {
        String prompt = """
                You are an expert meeting analyst.
                Summarize the following meeting transcript and return ONLY valid JSON in this exact format:
                {
                  "summary": "...",
                  "keyPoints": ["...", "..."],
                  "actionItems": ["...", "..."]
                }

                Rules:
                - Use concise business language.
                - Keep key points and action items short and specific.
                - If there are no action items, return an empty array.
                - Do not wrap the JSON in markdown fences.

                Transcript:
                %s
                """.formatted(transcript);

        try {
            String response = chatClientBuilder.build()
                    .prompt()
                    .user(prompt)
                    .call()
                    .content();

            if (response == null || response.isBlank()) {
                throw new AppException("AI summarization returned an empty response");
            }

            MeetingSummaryDto summaryDto = objectMapper.readValue(cleanJson(response), MeetingSummaryDto.class);
            if (summaryDto.getKeyPoints() == null) {
                summaryDto.setKeyPoints(List.of());
            }
            if (summaryDto.getActionItems() == null) {
                summaryDto.setActionItems(List.of());
            }
            return summaryDto;
        } catch (Exception ex) {
            log.error("Failed to summarize transcript", ex);
            throw new AppException("Failed to summarize transcript");
        }
    }

    private String cleanJson(String response) {
        String trimmed = response.trim();
        if (trimmed.startsWith("```")) {
            trimmed = trimmed.replaceFirst("^```json", "")
                    .replaceFirst("^```", "")
                    .replaceFirst("```$", "")
                    .trim();
        }
        return trimmed;
    }
}
