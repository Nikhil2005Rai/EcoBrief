package com.example.aimeetingtranscription.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class MeetingResponse {

    private Long id;
    private String title;
    private String filePath;
    private String transcript;
    private String summary;
    private List<String> keyPoints;
    private List<String> actionItems;
    private OffsetDateTime createdAt;
}
