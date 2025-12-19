package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.RequestStatus;
import ru.coursework.tutor_onl.model.ScheduleSlot;
import ru.coursework.tutor_onl.model.TutoringRequest;
import ru.coursework.tutor_onl.repository.ScheduleSlotRepository;
import ru.coursework.tutor_onl.repository.TutoringRequestRepository;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TutorRequestService {

    private final TutoringRequestRepository requestRepository;
    private final ScheduleSlotRepository slotRepository;

    public List<TutoringRequest> findIncoming(Long tutorId, RequestStatus status) {
        List<TutoringRequest> list = status == null
                ? requestRepository.findByTutorId(tutorId)
                : requestRepository.findByTutorIdAndStatus(tutorId, status);
        list.sort(Comparator.comparing(TutoringRequest::getCreatedAt).reversed());
        return list;
    }

    public List<TutoringRequest> findAll(RequestStatus status) {
        List<TutoringRequest> list = status == null
                ? requestRepository.findAll()
                : requestRepository.findAll().stream().filter(r -> r.getStatus() == status).toList();
        list.sort(Comparator.comparing(TutoringRequest::getCreatedAt).reversed());
        return list;
    }

    @Transactional
    public void changeStatus(Long tutorId, Long requestId, RequestStatus newStatus) {
        TutoringRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (!req.getTutor().getId().equals(tutorId)) {
            throw new IllegalStateException("Это не ваша заявка");
        }
        if (newStatus == RequestStatus.CONFIRMED && req.getStatus() != RequestStatus.NEW) {
            throw new IllegalStateException("Подтвердить можно только NEW");
        }
        if (newStatus == RequestStatus.DONE && req.getStatus() != RequestStatus.CONFIRMED) {
            throw new IllegalStateException("DONE только после CONFIRMED");
        }
        if (newStatus == RequestStatus.CANCELLED && req.getStatus() == RequestStatus.CANCELLED) {
            return;
        }
        req.setStatus(newStatus);
        requestRepository.save(req);
        if (newStatus == RequestStatus.CANCELLED && req.getSlot() != null) {
            ScheduleSlot slot = req.getSlot();
            slot.setAvailable(true);
            slotRepository.save(slot);
        }
    }
}
