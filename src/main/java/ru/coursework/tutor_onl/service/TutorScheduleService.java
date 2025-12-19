package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.ScheduleSlot;
import ru.coursework.tutor_onl.model.Subject;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.repository.ScheduleSlotRepository;
import ru.coursework.tutor_onl.repository.SubjectRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorScheduleService {

    private final ScheduleSlotRepository slotRepository;
    private final SubjectRepository subjectRepository;

    public List<ScheduleSlot> listSlots(TutorProfile tutor) {
        return slotRepository.findByTutorOrderByStartDateTimeAsc(tutor);
    }

    public List<ScheduleSlot> listAllSlots() {
        return slotRepository.findAll();
    }

    @Transactional
    public ScheduleSlot createSlot(TutorProfile tutor,
                                   LocalDateTime start,
                                   LocalDateTime end,
                                   Long subjectId) {
        if (end.isBefore(start) || end.isEqual(start)) {
            throw new IllegalArgumentException("Время окончания должно быть позже начала");
        }
        Subject subject = null;
        if (subjectId != null) {
            subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
        }
        ScheduleSlot slot = new ScheduleSlot();
        slot.setTutor(tutor);
        slot.setStartDateTime(start);
        slot.setEndDateTime(end);
        slot.setSubject(subject);
        slot.setAvailable(true);
        return slotRepository.save(slot);
    }

    @Transactional
    public void toggleAvailability(Long slotId, Long tutorId) {
        ScheduleSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Слот не найден"));
        if (!slot.getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Нельзя менять слот другого репетитора");
        }
        slot.setAvailable(!slot.isAvailable());
        slotRepository.save(slot);
    }

    @Transactional
    public void deleteSlot(Long slotId, Long tutorId) {
        ScheduleSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Слот не найден"));
        if (!slot.getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Нельзя удалить слот другого репетитора");
        }
        slotRepository.delete(slot);
    }
}
