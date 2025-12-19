package ru.coursework.tutor_onl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.tutor_onl.model.RequestStatus;
import ru.coursework.tutor_onl.model.TutoringRequest;

import java.util.List;

public interface TutoringRequestRepository extends JpaRepository<TutoringRequest, Long> {
    List<TutoringRequest> findByStudentId(Long studentId);
    List<TutoringRequest> findByTutorId(Long tutorId);
    List<TutoringRequest> findByTutorIdAndStatus(Long tutorId, RequestStatus status);
    List<TutoringRequest> findByStudentIdAndStatus(Long studentId, RequestStatus status);
}
