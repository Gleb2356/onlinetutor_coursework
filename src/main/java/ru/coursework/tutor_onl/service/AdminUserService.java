package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.Role;
import ru.coursework.tutor_onl.model.RoleName;
import ru.coursework.tutor_onl.model.User;
import ru.coursework.tutor_onl.repository.RoleRepository;
import ru.coursework.tutor_onl.repository.UserRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public List<User> listUsers(String query) {
        if (query == null || query.isBlank()) {
            return userRepository.findAll();
        }
        return userRepository.findByEmailContainingIgnoreCaseOrFullNameContainingIgnoreCase(query, query);
    }

    @Transactional
    public void changeRole(Long userId, RoleName roleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Роль не найдена"));
        user.getRoles().clear();
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @Transactional
    public void toggleBlocked(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        user.setBlocked(!user.isBlocked());
        userRepository.save(user);
    }
}
