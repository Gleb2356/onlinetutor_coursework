package ru.coursework.tutor_onl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.tutor_onl.model.ScheduleSlot;
import ru.coursework.tutor_onl.model.TutorProfile;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleSlotRepository extends JpaRepository<ScheduleSlot, Long> {
    List<ScheduleSlot> findByTutorOrderByStartDateTimeAsc(TutorProfile tutor);
    List<ScheduleSlot> findByTutorIdAndStartDateTimeBetween(Long tutorId, LocalDateTime from, LocalDateTime to);
}
