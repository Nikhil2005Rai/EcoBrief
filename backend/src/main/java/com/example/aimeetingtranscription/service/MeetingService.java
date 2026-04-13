package com.example.aimeetingtranscription.service;

import com.example.aimeetingtranscription.dto.meeting.MeetingListItemResponse;
import com.example.aimeetingtranscription.dto.meeting.MeetingResponse;
import com.example.aimeetingtranscription.dto.meeting.MeetingSummaryDto;
import com.example.aimeetingtranscription.entity.ActionItem;
import com.example.aimeetingtranscription.entity.Meeting;
import com.example.aimeetingtranscription.entity.User;
import com.example.aimeetingtranscription.exception.ResourceNotFoundException;
import com.example.aimeetingtranscription.repository.MeetingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetingService {

    private final MeetingRepository meetingRepository;
    private final CurrentUserService currentUserService;
    private final FileStorageService fileStorageService;
    private final TranscriptionService transcriptionService;
    private final AIService aiService;
    private final MeetingMapper meetingMapper;

    @Transactional
    public MeetingResponse uploadMeeting(MultipartFile file, String title) {
        User user = currentUserService.getCurrentUser();
        String storedPath = fileStorageService.store(file);
        String transcript = transcriptionService.transcribe(storedPath);
        MeetingSummaryDto summaryDto = aiService.summarizeTranscript(transcript);

        Meeting meeting = Meeting.builder()
                .user(user)
                .title(resolveTitle(file.getOriginalFilename(), title))
                .filePath(storedPath)
                .transcript(transcript)
                .summary(summaryDto.getSummary())
                .keyPoints(new ArrayList<>(summaryDto.getKeyPoints()))
                .createdAt(OffsetDateTime.now())
                .build();

        List<ActionItem> actionItems = new ArrayList<>();
        for (String item : summaryDto.getActionItems()) {
            actionItems.add(ActionItem.builder()
                    .meeting(meeting)
                    .text(item)
                    .build());
        }
        meeting.setActionItems(actionItems);

        return meetingMapper.toResponse(meetingRepository.save(meeting));
    }

    @Transactional
    public List<MeetingListItemResponse> getAllMeetings() {
        User user = currentUserService.getCurrentUser();
        return meetingRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(meetingMapper::toListItem)
                .toList();
    }

    @Transactional
    public MeetingResponse getMeeting(Long id) {
        User user = currentUserService.getCurrentUser();
        Meeting meeting = meetingRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Meeting not found"));
        return meetingMapper.toResponse(meeting);
    }

    private String resolveTitle(String originalFileName, String title) {
        if (title != null && !title.isBlank()) {
            return title.trim();
        }
        return originalFileName == null ? "Untitled Meeting" : originalFileName;
    }
}
