package ru.coursework.tutor_onl.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.coursework.tutor_onl.model.RequestStatus;
import ru.coursework.tutor_onl.model.Review;
import ru.coursework.tutor_onl.model.StudentProfile;
import ru.coursework.tutor_onl.model.TutoringRequest;
import ru.coursework.tutor_onl.model.User;
import ru.coursework.tutor_onl.repository.StudentProfileRepository;
import ru.coursework.tutor_onl.repository.UserRepository;
import ru.coursework.tutor_onl.service.CurrentUserService;
import ru.coursework.tutor_onl.service.StudentRequestService;
import ru.coursework.tutor_onl.service.StudentReviewService;
import ru.coursework.tutor_onl.web.dto.RequestCreateForm;

import java.util.List;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final CurrentUserService currentUserService;
    private final StudentRequestService studentRequestService;
    private final StudentReviewService studentReviewService;
    private final UserRepository userRepository;
    private final StudentProfileRepository studentProfileRepository;

    @GetMapping("/profile")
    public String profile(Model model) {
        User user = currentUserService.getCurrentUserOrThrow();
        StudentProfile profile = currentUserService.getCurrentStudentOrThrow();
        model.addAttribute("user", user);
        model.addAttribute("profile", profile);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String city,
                                @RequestParam(required = false) String goal,
                                RedirectAttributes redirectAttributes) {
        User user = currentUserService.getCurrentUserOrThrow();
        StudentProfile profile = currentUserService.getCurrentStudentOrThrow();
        user.setCity(city);
        profile.setGoal(goal);
        userRepository.save(user);
        studentProfileRepository.save(profile);
        redirectAttributes.addFlashAttribute("successMessage", "Профиль обновлён");
        return "redirect:/student/profile";
    }

    @GetMapping("/requests")
    public String myRequests(@RequestParam(value = "status", required = false) RequestStatus status, Model model) {
        StudentProfile student = currentUserService.getCurrentStudentOrThrow();
        List<TutoringRequest> requests = studentRequestService.findRequests(student.getId(), status);
        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("createForm", new RequestCreateForm());
        return "student/requests";
    }

    @PostMapping("/requests")
    public String createRequest(@ModelAttribute("createForm") @Valid RequestCreateForm form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {
        StudentProfile student = currentUserService.getCurrentStudentOrThrow();
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Проверьте данные заявки");
            return "redirect:/student/requests";
        }
        try {
            studentRequestService.createFromSlot(form.getSlotId(), student, form.getComment(), form.getDurationMinutes());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/student/requests";
        }
        redirectAttributes.addFlashAttribute("successMessage", "Заявка создана");
        return "redirect:/student/requests";
    }

    @PostMapping("/requests/{id}/cancel")
    public String cancelRequest(@PathVariable Long id, Model model) {
        StudentProfile student = currentUserService.getCurrentStudentOrThrow();
        try {
            studentRequestService.cancelRequest(id, student.getId());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/requests";
    }

    @GetMapping("/reviews")
    public String myReviews(Model model) {
        StudentProfile student = currentUserService.getCurrentStudentOrThrow();
        List<Review> myReviews = studentReviewService.findOwnReviews(student.getId());
        List<TutoringRequest> availableForReview = studentReviewService.requestsWithoutReview(student.getId());
        model.addAttribute("myReviews", myReviews);
        model.addAttribute("availableRequests", availableForReview);
        return "student/reviews";
    }

    @PostMapping("/reviews")
    public String addReview(@RequestParam Long requestId,
                            @RequestParam Integer rating,
                            @RequestParam(required = false) String comment,
                            Model model) {
        StudentProfile student = currentUserService.getCurrentStudentOrThrow();
        if (rating == null || rating < 1 || rating > 5) {
            model.addAttribute("errorMessage", "Рейтинг от 1 до 5");
            return "redirect:/student/reviews";
        }
        try {
            studentReviewService.addReview(student, requestId, rating, comment);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/reviews";
    }
}
