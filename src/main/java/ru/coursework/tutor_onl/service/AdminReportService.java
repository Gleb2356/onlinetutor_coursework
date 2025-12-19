package ru.coursework.tutor_onl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.repository.ReviewRepository;
import ru.coursework.tutor_onl.repository.SubjectRepository;
import ru.coursework.tutor_onl.repository.TutorProfileRepository;
import ru.coursework.tutor_onl.repository.TutoringRequestRepository;
import ru.coursework.tutor_onl.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminReportService {

    private final UserRepository userRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final SubjectRepository subjectRepository;
    private final TutoringRequestRepository tutoringRequestRepository;
    private final ReviewRepository reviewRepository;

    public Map<String, Object> metrics() {
        Map<String, Object> map = new HashMap<>();
        map.put("Пользователи", userRepository.count());
        map.put("Репетиторы", tutorProfileRepository.count());
        map.put("Предметы", subjectRepository.count());
        map.put("Заявки", tutoringRequestRepository.count());
        map.put("Отзывы", reviewRepository.count());
        return map;
    }
}
