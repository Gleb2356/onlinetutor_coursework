package ru.coursework.tutor_onl.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.repository.TutorProfileRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TutorSearchService {

    private final TutorProfileRepository tutorProfileRepository;

    public Page<TutorProfile> search(String subjectQuery,
                                     String city,
                                     Double priceFrom,
                                     Double priceTo,
                                     Double rating,
                                     String sort,
                                     Pageable pageable) {
        List<TutorProfile> all = tutorProfileRepository.findAll();

        List<TutorProfile> filtered = all.stream()
                .filter(tp -> city == null || city.isBlank() || (tp.getCity() != null && tp.getCity().toLowerCase().contains(city.toLowerCase())))
                .filter(tp -> subjectQuery == null || subjectQuery.isBlank() ||
                        tp.getSubjects().stream().anyMatch(s -> s.getName().toLowerCase().contains(subjectQuery.toLowerCase())))
                .filter(tp -> priceFrom == null || tp.getRatePerHour() == null || tp.getRatePerHour().doubleValue() >= priceFrom)
                .filter(tp -> priceTo == null || tp.getRatePerHour() == null || tp.getRatePerHour().doubleValue() <= priceTo)
                // rating stub — to be replaced when ratings are computed
                .collect(Collectors.toList());

        Comparator<TutorProfile> comparator = switch (sort == null ? "" : sort) {
            case "priceAsc" -> Comparator.comparing(tp -> tp.getRatePerHour() == null ? Double.MAX_VALUE : tp.getRatePerHour().doubleValue());
            case "priceDesc" -> Comparator.comparing((TutorProfile tp) -> tp.getRatePerHour() == null ? 0.0 : tp.getRatePerHour().doubleValue()).reversed();
            case "experience" -> Comparator.comparing(tp -> Objects.requireNonNullElse(tp.getExperienceYears(), 0));
            default -> Comparator.comparing(TutorProfile::getId);
        };

        List<TutorProfile> sorted = filtered.stream().sorted(comparator).collect(Collectors.toList());

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), sorted.size());
        List<TutorProfile> pageContent = start <= end ? sorted.subList(start, end) : List.of();
        return new PageImpl<>(pageContent, pageable, sorted.size());
    }
}
