package com.samsamhajo.deepground.calendar.repository;

import com.samsamhajo.deepground.calendar.entity.MemberStudySchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberStudyScheduleRepository extends JpaRepository<MemberStudySchedule, Long> {

    void deleteAllByStudyScheduleId(Long scheduleId);
}
