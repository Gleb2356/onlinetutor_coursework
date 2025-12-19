package ru.coursework.tutor_onl.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.Subject;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.repository.SubjectRepository;
import ru.coursework.tutor_onl.repository.TutorProfileRepository;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TutorProfileService {

    private final TutorProfileRepository tutorProfileRepository;
    private final SubjectRepository subjectRepository;

    public List<Subject> allSubjects() {
        return subjectRepository.findAll();
    }

    @Transactional
    public TutorProfile updateProfile(TutorProfile profile,
                                      String bio,
                                      String education,
                                      Integer experienceYears,
                                      String city,
                                      BigDecimal ratePerHour,
                                      List<Long> subjectIds) {
        profile.setBio(bio);
        profile.setEducation(education);
        profile.setExperienceYears(experienceYears);
        profile.setCity(city);
        profile.setRatePerHour(ratePerHour);
        Set<Subject> subjects = new HashSet<>();
        if (subjectIds != null && !subjectIds.isEmpty()) {
            subjects.addAll(subjectRepository.findAllById(subjectIds));
        }
        profile.setSubjects(subjects);
        return tutorProfileRepository.save(profile);
    }
}
