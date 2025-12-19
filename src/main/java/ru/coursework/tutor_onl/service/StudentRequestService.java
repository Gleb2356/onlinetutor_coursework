package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.*;
import ru.coursework.tutor_onl.repository.ScheduleSlotRepository;
import ru.coursework.tutor_onl.repository.TutoringRequestRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentRequestService {

    private final TutoringRequestRepository requestRepository;
    private final ScheduleSlotRepository slotRepository;

    public List<TutoringRequest> findRequests(Long studentId, RequestStatus status) {
        List<TutoringRequest> list = status == null
                ? requestRepository.findByStudentId(studentId)
                : requestRepository.findByStudentIdAndStatus(studentId, status);
        list.sort(Comparator.comparing(TutoringRequest::getCreatedAt).reversed());
        return list;
    }

    @Transactional
    public TutoringRequest createFromSlot(Long slotId, StudentProfile student, String comment, Integer durationMinutes) {
        ScheduleSlot slot = slotRepository.findById(slotId)
                .orElseThrow(() -> new IllegalArgumentException("Слот не найден"));
        if (!slot.isAvailable()) {
            throw new IllegalStateException("Слот уже занят");
        }
        TutorProfile tutor = slot.getTutor();
        Subject subject = slot.getSubject();
        if (subject == null) {
            throw new IllegalStateException("У слота не указан предмет");
        }
        TutoringRequest req = new TutoringRequest();
        req.setStudent(student);
        req.setTutor(tutor);
        req.setSubject(subject);
        req.setSlot(slot);
        req.setStatus(RequestStatus.NEW);
        req.setStartDateTime(slot.getStartDateTime());
        req.setDurationMinutes(Optional.ofNullable(durationMinutes).orElse(
                (int) Duration.between(slot.getStartDateTime(), slot.getEndDateTime()).toMinutes()));
        if (tutor.getRatePerHour() != null) {
            double hours = req.getDurationMinutes() / 60.0;
            req.setPrice(tutor.getRatePerHour().multiply(BigDecimal.valueOf(hours)));
        }
        req.setComment(comment);
        slot.setAvailable(false);
        slotRepository.save(slot);
        return requestRepository.save(req);
    }

    @Transactional
    public void cancelRequest(Long requestId, Long studentId) {
        TutoringRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (!req.getStudent().getId().equals(studentId)) {
            throw new IllegalStateException("Это не ваша заявка");
        }
        if (!(req.getStatus() == RequestStatus.NEW || req.getStatus() == RequestStatus.CONFIRMED)) {
            throw new IllegalStateException("Отменить можно только NEW или CONFIRMED");
        }
        req.setStatus(RequestStatus.CANCELLED);
        ScheduleSlot slot = req.getSlot();
        if (slot != null) {
            slot.setAvailable(true);
            slotRepository.save(slot);
        }
        requestRepository.save(req);
    }
}
