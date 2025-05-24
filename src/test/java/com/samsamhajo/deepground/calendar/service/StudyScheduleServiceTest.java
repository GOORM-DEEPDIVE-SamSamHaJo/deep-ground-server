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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudyScheduleServiceTest {

    @Mock
    private StudyScheduleRepository studyScheduleRepository;

    @Mock
    private StudyGroupRepository studyGroupRepository;

    @Mock
    private MemberStudyScheduleRepository memberStudyScheduleRepository;

    @InjectMocks
    private StudyScheduleService studyScheduleService;

    private StudyGroup studyGroup;
    private StudyScheduleRequestDto requestDto;

    @BeforeEach
    void setup() {
        studyGroup = mock(StudyGroup.class);
        requestDto = StudyScheduleRequestDto.builder()
                .title("스터디 스케줄 1")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .description("스터디 설명")
                .location("온라인")
                .build();
    }

    @Test
    @DisplayName("스터디 일정 생성 성공")
    void createStudySchedule_Success() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        when(studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                anyLong(),
                eq(requestDto.getStartTime()),
                eq(requestDto.getEndTime())
        )).thenReturn(false);

        when(studyScheduleRepository.save(any(StudySchedule.class)))
                .thenAnswer(invocation -> {
                    StudySchedule schedule = invocation.getArgument(0);

                    Field idField = StudySchedule.class.getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(schedule, 1L);

                    return schedule;
                });

        // when
        StudyScheduleResponseDto responseDto = studyScheduleService.createStudySchedule(1L, requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getTitle()).isEqualTo(requestDto.getTitle());
        assertThat(responseDto.getStartTime()).isEqualTo(requestDto.getStartTime());
        assertThat(responseDto.getEndTime()).isEqualTo(requestDto.getEndTime());
        assertThat(responseDto.getDescription()).isEqualTo(requestDto.getDescription());
        assertThat(responseDto.getLocation()).isEqualTo(requestDto.getLocation());

        verify(studyGroupRepository, times(1)).findById(anyLong());
        verify(studyScheduleRepository, times(1)).existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                anyLong(),
                eq(requestDto.getStartTime()),
                eq(requestDto.getEndTime())
        );
        verify(studyScheduleRepository, times(1)).save(any(StudySchedule.class));
    }

    @Test
    @DisplayName("스터디 일정 생성 실패 - 종료 시간이 시작 시간보다 빠른 경우")
    void createStudySchedule_Fail_EndTimeBeforeStartTime() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));

        LocalDateTime startTime = LocalDateTime.of(2025, 5, 20, 15, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 20, 13, 0);

        requestDto = requestDto.toBuilder()
                .startTime(startTime)
                .endTime(endTime)
                .build();

        // when & then
        assertThatThrownBy(() -> studyScheduleService.createStudySchedule(1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.INVALID_DATE_RANGE.getMessage());

        verify(studyScheduleRepository, never()).save(any(StudySchedule.class));
    }

    @Test
    @DisplayName("스터디 일정 생성 실패 - 존재하지 않는 스터디 그룹으로 생성 요청")
    void createStudySchedule_Fail_StudyGroupNotFound() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyScheduleService.createStudySchedule(1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.STUDY_GROUP_NOT_FOUND.getMessage());

        verify(studyScheduleRepository, never()).save(any(StudySchedule.class));
    }

    @Test
    @DisplayName("스터디 일정 생성 실패 - 중복된 시간대의 일정이 존재할 경우")
    void createStudySchedule_Fail_DuplicateSchedule() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        when(studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                anyLong(),
                eq(requestDto.getStartTime()),
                eq(requestDto.getEndTime())
        )).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> studyScheduleService.createStudySchedule(1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.DUPLICATE_SCHEDULE.getMessage());

        verify(studyScheduleRepository, never()).save(any(StudySchedule.class));
    }


    @Test
    @DisplayName("스터디 그룹 ID로 일정 조회 성공")
    void findSchedulesByStudyGroupId_Success() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        StudySchedule studySchedule1 = StudySchedule.of(studyGroup, "스터디 1", LocalDateTime.now(), LocalDateTime.now().plusHours(1), "설명 1", "온라인");
        StudySchedule studySchedule2 = StudySchedule.of(studyGroup, "스터디 2", LocalDateTime.now().plusHours(2), LocalDateTime.now().plusHours(3), "설명 2", "오프라인");

        when(studyScheduleRepository.findAllByStudyGroupId(anyLong())).thenReturn(List.of(studySchedule1, studySchedule2));

        // when
        List<StudyScheduleResponseDto> responseDtos = studyScheduleService.findSchedulesByStudyGroupId(1L);

        // then
        assertThat(responseDtos).hasSize(2);
        assertThat(responseDtos.get(0).getTitle()).isEqualTo("스터디 1");
        assertThat(responseDtos.get(1).getTitle()).isEqualTo("스터디 2");

        verify(studyScheduleRepository, times(1)).findAllByStudyGroupId(anyLong());

    }

    @Test
    @DisplayName("스터디 그룹 ID로 일정 조회 실패 - 존재하지 않는 스터디 그룹 ID로 조회")
    void findSchedulesByStudyGroupId_StudyGroupNotFound() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyScheduleService.findSchedulesByStudyGroupId(1L))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.STUDY_GROUP_NOT_FOUND.getMessage());

        verify(studyScheduleRepository, never()).findAllByStudyGroupId(anyLong());

    }



    @Test
    @DisplayName("스터디 일정 수정 성공")
    void updateStudySchedule_Success() throws NoSuchFieldException, IllegalAccessException {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));

        StudySchedule existingSchedule = StudySchedule.of(
                studyGroup,
                "기존 제목",
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getDescription(),
                requestDto.getLocation()
        );

        Field idField = StudySchedule.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(existingSchedule, 1L);

        when(studyScheduleRepository.findById(anyLong())).thenReturn(Optional.of(existingSchedule));

        StudyScheduleRequestDto updateRequestDto = StudyScheduleRequestDto.builder()
                .title("수정된 일정 제목")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .description("수정된 일정 설명")
                .location("Online")
                .build();
        // when
        StudyScheduleResponseDto responseDto = studyScheduleService.updateStudySchedule(studyGroup.getId(), existingSchedule.getId(), updateRequestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(existingSchedule.getId());
        assertThat(responseDto.getTitle()).isEqualTo(updateRequestDto.getTitle());
    }

    @Test
    @DisplayName("스터디 일정 수정 실패 - 존재하지 않는 스터디 그룹")
    void updateStudySchedule_Fail_StudyGroupNotFound() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyScheduleService.updateStudySchedule(1L, 1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.STUDY_GROUP_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("스터디 일정 수정 실패 - 존재하지 않는 스터디 일정")
    void updateStudySchedule_Fail_ScheduleNotFound() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        when(studyScheduleRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyScheduleService.updateStudySchedule(1L, 1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.SCHEDULE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("스터디 일정 수정 실패 - 종료 시간이 시작 시간보다 빠른 경우")
    void updateStudySchedule_Fail_EndTimeBeforeStartTime() throws IllegalAccessException, NoSuchFieldException {
        // given
        LocalDateTime startTime = LocalDateTime.of(2025, 5, 21, 13, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 5, 21, 11, 0);

        StudyScheduleRequestDto request = requestDto.toBuilder()
                .startTime(startTime)
                .endTime(endTime)
                .build();

        StudySchedule schedule = StudySchedule.of(
                studyGroup,
                "제목",
                requestDto.getStartTime(),
                requestDto.getEndTime(),
                requestDto.getDescription(),
                requestDto.getLocation()
        );

        Field idField = StudySchedule.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(schedule, 1L);

        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        when(studyScheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                anyLong(), any(), any()
        )).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> studyScheduleService.updateStudySchedule(1L, 1L, request))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.INVALID_DATE_RANGE.getMessage());
    }

    @Test
    @DisplayName("스터디 일정 수정 실패 - 중복된 시간대의 일정이 존재할 경우")
    void updateStudySchedule_Fail_DuplicateSchedule() {
        // given
        when(studyGroupRepository.findById(anyLong())).thenReturn(Optional.of(studyGroup));
        StudySchedule schedule = mock(StudySchedule.class);
        when(schedule.getId()).thenReturn(2L);

        when(studyScheduleRepository.findById(anyLong())).thenReturn(Optional.of(schedule));
        when(studyScheduleRepository.existsByStudyGroupIdAndEndTimeGreaterThanAndStartTimeLessThan(
                anyLong(),
                eq(requestDto.getStartTime()),
                eq(requestDto.getEndTime())
        )).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> studyScheduleService.updateStudySchedule(1L, 1L, requestDto))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.DUPLICATE_SCHEDULE.getMessage());
    }

    @Test
    @DisplayName("스터디 일정 삭제 성공")
    void deleteStudySchedule_Success() {
        // given
        Long studyGroupId = 1L;
        Long scheduleId = 1L;

        StudyGroup studyGroup = mock(StudyGroup.class);
        when(studyGroup.getId()).thenReturn(studyGroupId);

        StudySchedule schedule = mock(StudySchedule.class);
        when(schedule.getStudyGroup()).thenReturn(studyGroup);
        when(studyScheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        // when
        studyScheduleService.deleteStudySchedule(studyGroupId, scheduleId);

        // then
        verify(memberStudyScheduleRepository).deleteAllByStudyScheduleId(schedule.getId());
        verify(studyScheduleRepository).delete(schedule);
    }

    @Test
    @DisplayName("스터디 일정 삭제 실패 - 존재하지 않는 스터디 일정")
    void deleteStudySchedule_Fail_StudyScheduleNotFound() {
        // given
        Long studyGroupId = 1L;
        Long scheduleId = 1L;

        when(studyScheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> studyScheduleService.deleteStudySchedule(studyGroupId, scheduleId))
                .isInstanceOf(ScheduleException.class)
                .hasMessageContaining(ScheduleErrorCode.SCHEDULE_NOT_FOUND.getMessage());

        verify(studyScheduleRepository, never()).delete(any());
    }
}