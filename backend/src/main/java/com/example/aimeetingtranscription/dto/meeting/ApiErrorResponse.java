package com.example.aimeetingtranscription.dto.meeting;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class ApiErrorResponse {

    private String message;
    private int status;
    private OffsetDateTime timestamp;
    private Map<String, String> validationErrors;
}
