package ru.coursework.tutor_onl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.StudentProfile;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.model.User;
import ru.coursework.tutor_onl.repository.StudentProfileRepository;
import ru.coursework.tutor_onl.repository.TutorProfileRepository;
import ru.coursework.tutor_onl.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final TutorProfileRepository tutorProfileRepository;

    public User getCurrentUserOrThrow() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }
        return userRepository.findByEmailIgnoreCase(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
    }

    public StudentProfile getCurrentStudentOrThrow() {
        User user = getCurrentUserOrThrow();
        return studentProfileRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    StudentProfile profile = new StudentProfile();
                    profile.setUser(user);
                    profile.setGoal(null);
                    return studentProfileRepository.save(profile);
                });
    }

    public TutorProfile getCurrentTutorOrThrow() {
        User user = getCurrentUserOrThrow();
        return tutorProfileRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalStateException("Профиль репетитора не найден"));
    }

    public TutorProfile getCurrentTutorOrNull() {
        User user = getCurrentUserOrThrow();
        return tutorProfileRepository.findByUserId(user.getId()).orElse(null);
    }
}
