package ru.coursework.tutor_onl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.coursework.tutor_onl.model.Review;
import ru.coursework.tutor_onl.model.ScheduleSlot;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.repository.ReviewRepository;
import ru.coursework.tutor_onl.repository.ScheduleSlotRepository;
import ru.coursework.tutor_onl.repository.TutorProfileRepository;
import ru.coursework.tutor_onl.service.TutorSearchService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class TutorController {

    private final TutorSearchService tutorSearchService;
    private final TutorProfileRepository tutorProfileRepository;
    private final ScheduleSlotRepository scheduleSlotRepository;
    private final ReviewRepository reviewRepository;

    @GetMapping("/tutors")
    public String listTutors(@RequestParam(value = "subject", required = false) String subject,
                             @RequestParam(value = "city", required = false) String city,
                             @RequestParam(value = "priceFrom", required = false) Double priceFrom,
                             @RequestParam(value = "priceTo", required = false) Double priceTo,
                             @RequestParam(value = "rating", required = false) Double rating,
                             @RequestParam(value = "sort", required = false) String sort,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "6") int size,
                             Model model) {
        Page<TutorProfile> tutors = tutorSearchService.search(subject, city, priceFrom, priceTo, rating, sort, PageRequest.of(page, size));
        model.addAttribute("tutors", tutors);
        model.addAttribute("subject", subject);
        model.addAttribute("city", city);
        model.addAttribute("priceFrom", priceFrom);
        model.addAttribute("priceTo", priceTo);
        model.addAttribute("rating", rating);
        model.addAttribute("sort", sort);
        return "tutors/list";
    }

    @GetMapping("/tutors/{id}")
    public String tutorDetails(@PathVariable Long id, Model model) {
        TutorProfile tutor = tutorProfileRepository.findById(id).orElse(null);
        if (tutor == null) {
            return "redirect:/error/404";
        }
        List<ScheduleSlot> slots = scheduleSlotRepository.findByTutorOrderByStartDateTimeAsc(tutor);
        List<Review> reviews = reviewRepository.findByTutorIdOrderByCreatedAtDesc(tutor.getId());
        double avgRating = reviews.isEmpty() ? 0.0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);

        model.addAttribute("tutor", tutor);
        model.addAttribute("slots", slots);
        model.addAttribute("reviews", reviews);
        model.addAttribute("avgRating", avgRating);
        return "tutors/detail";
    }
}
