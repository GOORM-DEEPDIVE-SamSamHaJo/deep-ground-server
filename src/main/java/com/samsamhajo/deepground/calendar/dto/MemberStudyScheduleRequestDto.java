package com.samsamhajo.deepground.calendar.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class MemberStudyScheduleRequestDto {

    private Boolean isAvailable;
    private Boolean isImportant;
    private String memo;
}
