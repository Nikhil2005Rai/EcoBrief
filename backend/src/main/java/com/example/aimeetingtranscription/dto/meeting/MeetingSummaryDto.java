package com.example.aimeetingtranscription.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetingSummaryDto {

    private String summary;
    private List<String> keyPoints;
    private List<String> actionItems;
}
