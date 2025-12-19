package ru.coursework.tutor_onl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.coursework.tutor_onl.model.*;
import ru.coursework.tutor_onl.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final SubjectRepository subjectRepository;
    private final TutorProfileRepository tutorProfileRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            initRoles();
            initSubjects();
            initUsers();
        };
    }

    private void initRoles() {
        for (RoleName rn : RoleName.values()) {
            roleRepository.findByName(rn).orElseGet(() -> roleRepository.save(new Role(null, rn)));
        }
    }

    private void initSubjects() {
        List<String> baseSubjects = List.of("Математика", "Английский язык", "Физика", "Информатика");
        for (String name : baseSubjects) {
            subjectRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> subjectRepository.save(new Subject(null, name, null)));
        }
    }

    private void initUsers() {
        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow();
        Role tutorRole = roleRepository.findByName(RoleName.ROLE_TUTOR).orElseThrow();
        Role studentRole = roleRepository.findByName(RoleName.ROLE_STUDENT).orElseThrow();

        userRepository.findByEmailIgnoreCase("admin@tutor.ru").orElseGet(() -> {
            User admin = new User();
            admin.setEmail("admin@tutor.ru");
            admin.setFullName("Admin User");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEnabled(true);
            admin.setBlocked(false);
            admin.setRoles(new java.util.HashSet<>(Set.of(adminRole)));
            return userRepository.save(admin);
        });

        User tutor = userRepository.findByEmailIgnoreCase("tutor@tutor.ru").orElseGet(() -> {
            User u = new User();
            u.setEmail("tutor@tutor.ru");
            u.setFullName("Demo Tutor");
            u.setCity("Москва");
            u.setPassword(passwordEncoder.encode("tutor123"));
            u.setEnabled(true);
            u.setBlocked(false);
            u.setRoles(new java.util.HashSet<>(Set.of(tutorRole)));
            return userRepository.save(u);
        });

        TutorProfile tutorProfile = tutorProfileRepository.findByUserId(tutor.getId()).orElseGet(() -> {
            TutorProfile profile = new TutorProfile();
            profile.setUser(tutor);
            profile.setBio("Опытный репетитор, помогу подготовиться к экзаменам.");
            profile.setEducation("Педагогический университет");
            profile.setExperienceYears(5);
            profile.setCity(tutor.getCity());
            profile.setRatePerHour(new BigDecimal("1200"));
            profile.setSubjects(Set.copyOf(subjectRepository.findAll().subList(0, Math.min(2, (int) subjectRepository.count()))));
            return tutorProfileRepository.save(profile);
        });

        User student = userRepository.findByEmailIgnoreCase("student@tutor.ru").orElseGet(() -> {
            User u = new User();
            u.setEmail("student@tutor.ru");
            u.setFullName("Demo Student");
            u.setCity("Санкт-Петербург");
            u.setPassword(passwordEncoder.encode("student123"));
            u.setEnabled(true);
            u.setBlocked(false);
            u.setRoles(new java.util.HashSet<>(Set.of(studentRole)));
            return userRepository.save(u);
        });

        studentProfileRepository.findByUserId(student.getId()).orElseGet(() -> {
            StudentProfile profile = new StudentProfile();
            profile.setUser(student);
            profile.setGoal("Подготовка к экзамену");
            return studentProfileRepository.save(profile);
        });

        // демо-слот
        if (!scheduleSlotRepository.findByTutorOrderByStartDateTimeAsc(tutorProfile).isEmpty()) {
            return;
        }
        ScheduleSlot slot = new ScheduleSlot();
        slot.setTutor(tutorProfile);
        slot.setSubject(subjectRepository.findAll().stream().findFirst().orElse(null));
        slot.setStartDateTime(LocalDateTime.now().plusDays(1).withHour(15).withMinute(0));
        slot.setEndDateTime(LocalDateTime.now().plusDays(1).withHour(16).withMinute(0));
        slot.setAvailable(true);
        scheduleSlotRepository.save(slot);
    }
}
