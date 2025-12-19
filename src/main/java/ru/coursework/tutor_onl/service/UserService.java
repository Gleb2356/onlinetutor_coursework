package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.Role;
import ru.coursework.tutor_onl.model.RoleName;
import ru.coursework.tutor_onl.model.StudentProfile;
import ru.coursework.tutor_onl.model.User;
import ru.coursework.tutor_onl.repository.RoleRepository;
import ru.coursework.tutor_onl.repository.StudentProfileRepository;
import ru.coursework.tutor_onl.repository.UserRepository;
import ru.coursework.tutor_onl.web.dto.RegistrationForm;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerStudent(RegistrationForm form) {
        if (userRepository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT)
                .orElseThrow(() -> new IllegalStateException("Роль STUDENT не найдена"));

        User user = new User();
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setFullName(form.getFullName().trim());
        user.setCity(form.getCity());
        user.setEnabled(true);
        user.setBlocked(false);
        user.setRoles(new java.util.HashSet<>(Set.of(studentRole)));
        User saved = userRepository.save(user);

        StudentProfile profile = new StudentProfile();
        profile.setUser(saved);
        profile.setGoal(form.getGoal());
        studentProfileRepository.save(profile);
        return saved;
    }
}
