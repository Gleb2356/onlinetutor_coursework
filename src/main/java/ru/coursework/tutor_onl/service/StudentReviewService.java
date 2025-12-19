package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.*;
import ru.coursework.tutor_onl.repository.ReviewRepository;
import ru.coursework.tutor_onl.repository.TutoringRequestRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentReviewService {

    private final TutoringRequestRepository requestRepository;
    private final ReviewRepository reviewRepository;

    public List<TutoringRequest> findDoneRequests(Long studentId) {
        return requestRepository.findByStudentIdAndStatus(studentId, RequestStatus.DONE);
    }

    public List<Review> findOwnReviews(Long studentId) {
        return reviewRepository.findByStudentId(studentId);
    }

    @Transactional
    public Review addReview(StudentProfile student, Long requestId, Integer rating, String comment) {
        TutoringRequest req = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));
        if (!req.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Это не ваша заявка");
        }
        if (req.getStatus() != RequestStatus.DONE) {
            throw new IllegalStateException("Оставлять отзыв можно только по завершённым заявкам");
        }
        Review review = new Review();
        review.setTutor(req.getTutor());
        review.setStudent(student);
        review.setRating(rating);
        review.setComment(comment);
        return reviewRepository.save(review);
    }

    public List<TutoringRequest> requestsWithoutReview(Long studentId) {
        List<TutoringRequest> done = findDoneRequests(studentId);
        Set<Long> reviewedTutors = findOwnReviews(studentId).stream()
                .map(r -> r.getTutor().getId())
                .collect(Collectors.toSet());
        return done.stream()
                .filter(req -> !reviewedTutors.contains(req.getTutor().getId()))
                .toList();
    }
}
