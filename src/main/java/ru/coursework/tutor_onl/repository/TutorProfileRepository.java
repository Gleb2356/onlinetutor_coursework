package ru.coursework.tutor_onl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.coursework.tutor_onl.model.TutorProfile;

import java.util.Optional;

public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {
    Optional<TutorProfile> findByUserId(Long userId);
}
