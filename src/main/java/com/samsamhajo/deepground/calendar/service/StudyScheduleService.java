package com.samsamhajo.deepground.calendar.service;

import com.samsamhajo.deepground.calendar.dto.StudyScheduleRequestDto;
import com.samsamhajo.deepground.calendar.dto.StudyScheduleResponseDto;
import com.samsamhajo.deepground.calendar.entity.StudySchedule;
import com.samsamhajo.deepground.calendar.exception.ScheduleErrorCode;
import com.samsamhajo.deepground.calendar.exception.ScheduleException;
import com.samsamhajo.deepground.calendar.repository.MemberStudyScheduleRepository;
import com.samsamhajo.deepground.calendar.repository.StudyScheduleRepository;
import com.samsamhajo.deepground.studyGroup.entity.StudyGroup;
import com.samsamhajo.deepground.studyGroup.repository.StudyGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyScheduleService {

    private final StudyScheduleRepository studyScheduleRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final MemberStudyScheduleRepository memberStudyScheduleRepository;

    @Transactional
    public StudyScheduleResponseDto createStudySchedule(Long studyGroupId, StudyScheduleRequestDto requestDto) {

        StudyGroup studyGroup = validateStudyGroup(studyGroupId);
        validateSchedule(studyGroupId, requestDto);

        StudySchedule studySchedule = StudySchedule.of(
                studyGroup,
                requestDto.getTitle(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getDescription(),
                requestDto.getLocation()
        );

        StudySchedule savedSchedule = studyScheduleRepository.save(studySchedule);

        return StudyScheduleResponseDto.from(savedSchedule);
    }

    @Transactional(readOnly = true)
    public List<StudyScheduleResponseDto> findSchedulesByStudyGroupId(Long studyGroupId) {

        validateStudyGroup(studyGroupId);

        List<StudySchedule> schedules = studyScheduleRepository.findAllByStudyGroupId(studyGroupId);

        return schedules.stream()
                .map(StudyScheduleResponseDto::from)
                .toList();
    }

    @Transactional
    public StudyScheduleResponseDto updateStudySchedule(Long studyGroupId, Long scheduleId, StudyScheduleRequestDto requestDto) {

        validateStudyGroup(studyGroupId);

        StudySchedule studySchedule = studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        boolean isDuplicated = studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                studyGroupId,
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );

        // 자기 자신은 제외
        if (isDuplicated && !studySchedule.getId().equals(scheduleId)) {
            throw new ScheduleException(ScheduleErrorCode.DUPLICATE_SCHEDULE);
        }

        studySchedule.update(
                requestDto.getTitle(),
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getDescription(),
                requestDto.getLocation()
        );

        return StudyScheduleResponseDto.from(studySchedule);
    }

    @Transactional
    public void deleteStudySchedule(Long studyGroupId, Long scheduleId) {
        StudySchedule schedule = studyScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));

        if (!schedule.getStudyGroup().getId().equals(studyGroupId)) {
            throw new ScheduleException(ScheduleErrorCode.MISMATCHED_GROUP);
        }

        memberStudyScheduleRepository.deleteAllByStudyScheduleId(schedule.getId());

        studyScheduleRepository.delete(schedule);
    }

    private StudyGroup validateStudyGroup(Long studyGroupId) {
        return studyGroupRepository.findById(studyGroupId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.STUDY_GROUP_NOT_FOUND));
    }

    private void validateSchedule(Long studyGroupId, StudyScheduleRequestDto requestDto) {
        if (requestDto.getEndTime().isBefore(requestDto.getStartTime())) {
            throw new ScheduleException(ScheduleErrorCode.INVALID_DATE_RANGE);
        }
        boolean isDuplicated = studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                studyGroupId,
                requestDto.getStartTime(),
                requestDto.getEndTime()
        );

        if (isDuplicated) {
            throw new ScheduleException(ScheduleErrorCode.DUPLICATE_SCHEDULE);

        }
    }
}