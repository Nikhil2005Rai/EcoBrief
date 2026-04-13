package com.example.aimeetingtranscription.controller;

import com.example.aimeetingtranscription.dto.meeting.MeetingListItemResponse;
import com.example.aimeetingtranscription.dto.meeting.MeetingResponse;
import com.example.aimeetingtranscription.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MeetingResponse> uploadMeeting(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "title", required = false) String title) {
        return ResponseEntity.ok(meetingService.uploadMeeting(file, title));
    }

    @GetMapping("/all")
    public ResponseEntity<List<MeetingListItemResponse>> getAllMeetings() {
        return ResponseEntity.ok(meetingService.getAllMeetings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MeetingResponse> getMeeting(@PathVariable Long id) {
        return ResponseEntity.ok(meetingService.getMeeting(id));
    }
}
