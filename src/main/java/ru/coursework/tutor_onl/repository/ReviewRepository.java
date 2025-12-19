package ru.coursework.tutor_onl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.tutor_onl.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTutorIdOrderByCreatedAtDesc(Long tutorId);
    List<Review> findByStudentId(Long studentId);
}
