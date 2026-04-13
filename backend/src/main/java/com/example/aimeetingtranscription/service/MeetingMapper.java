package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.dto.meeting.MeetingListItemResponse;
import com.example.aimeetingtranscription.dto.meeting.MeetingResponse;
import com.example.aimeetingtranscription.entity.Meeting;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MeetingMapper {

    public MeetingResponse toResponse(Meeting meeting) {
        return MeetingResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .filePath(meeting.getFilePath())
                .transcript(meeting.getTranscript())
                .summary(meeting.getSummary())
                .keyPoints(new ArrayList<>(meeting.getKeyPoints()))
                .actionItems(meeting.getActionItems().stream().map(actionItem -> actionItem.getText()).toList())
                .createdAt(meeting.getCreatedAt())
                .build();
    }

    public MeetingListItemResponse toListItem(Meeting meeting) {
        return MeetingListItemResponse.builder()
                .id(meeting.getId())
                .title(meeting.getTitle())
                .summary(meeting.getSummary())
                .actionItems(meeting.getActionItems().stream().map(actionItem -> actionItem.getText()).toList())
                .createdAt(meeting.getCreatedAt())
                .build();
    }
}
