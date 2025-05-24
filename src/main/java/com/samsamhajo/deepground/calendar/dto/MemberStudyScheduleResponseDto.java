package com.samsamhajo.deepground.calendar.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberStudyScheduleResponseDto {

    private Long id;
    private Boolean isAvailable;
    private Boolean isImportant;
    private String memo;
}
