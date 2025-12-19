package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.Subject;
import ru.coursework.tutor_onl.repository.SubjectRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> list(String query) {
        if (query == null || query.isBlank()) {
            return subjectRepository.findAll();
        }
        return subjectRepository.findByNameContainingIgnoreCase(query);
    }

    @Transactional
    public Subject create(String name, String description) {
        if (subjectRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new IllegalArgumentException("Предмет уже существует");
        }
        Subject s = new Subject();
        s.setName(name);
        s.setDescription(description);
        return subjectRepository.save(s);
    }

    @Transactional
    public Subject update(Long id, String name, String description) {
        Subject s = subjectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Предмет не найден"));
        s.setName(name);
        s.setDescription(description);
        return subjectRepository.save(s);
    }

    @Transactional
    public void delete(Long id) {
        subjectRepository.deleteById(id);
    }
}
