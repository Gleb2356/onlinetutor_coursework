package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.Review;
import ru.coursework.tutor_onl.repository.ReviewRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReviewService {

    private final ReviewRepository reviewRepository;

    public List<Review> listAll() {
        return reviewRepository.findAll();
    }

    @Transactional
    public void hide(Long id) {
        Review r = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Отзыв не найден"));
        r.setVisible(false);
        reviewRepository.save(r);
    }

    @Transactional
    public void unhide(Long id) {
        Review r = reviewRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Отзыв не найден"));
        r.setVisible(true);
        reviewRepository.save(r);
    }

    @Transactional
    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }
}
