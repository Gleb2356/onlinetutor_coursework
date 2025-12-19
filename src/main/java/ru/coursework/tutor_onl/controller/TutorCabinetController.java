package ru.coursework.tutor_onl.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.coursework.tutor_onl.model.RequestStatus;
import ru.coursework.tutor_onl.model.Subject;
import ru.coursework.tutor_onl.model.TutorProfile;
import ru.coursework.tutor_onl.model.TutoringRequest;
import ru.coursework.tutor_onl.service.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/tutor")
@RequiredArgsConstructor
public class TutorCabinetController {

    private final CurrentUserService currentUserService;
    private final TutorProfileService tutorProfileService;
    private final TutorScheduleService tutorScheduleService;
    private final TutorRequestService tutorRequestService;

    @GetMapping("/profile")
    public String profile(Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        List<Subject> subjects = tutorProfileService.allSubjects();
        model.addAttribute("tutor", tutor);
        model.addAttribute("subjects", subjects);
        return "tutor/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@RequestParam(required = false) String bio,
                                @RequestParam(required = false) String education,
                                @RequestParam(required = false) Integer experienceYears,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false) BigDecimal ratePerHour,
                                @RequestParam(value = "subjectIds", required = false) List<Long> subjectIds,
                                Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        tutorProfileService.updateProfile(tutor, bio, education, experienceYears, city, ratePerHour, subjectIds);
        return "redirect:/tutor/profile";
    }

    @GetMapping("/schedule")
    public String schedule(Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrNull();
        boolean isAdminView = tutor == null;
        model.addAttribute("isAdminView", isAdminView);
        model.addAttribute("slots", isAdminView ? tutorScheduleService.listAllSlots() : tutorScheduleService.listSlots(tutor));
        model.addAttribute("subjects", tutorProfileService.allSubjects());
        return "tutor/schedule";
    }

    @PostMapping("/schedule")
    public String addSlot(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
                          @RequestParam(required = false) Long subjectId,
                          Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        try {
            tutorScheduleService.createSlot(tutor, start, end, subjectId);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tutor/schedule";
    }

    @PostMapping("/schedule/{id}/toggle")
    public String toggleSlot(@PathVariable Long id, Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        try {
            tutorScheduleService.toggleAvailability(id, tutor.getId());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tutor/schedule";
    }

    @PostMapping("/schedule/{id}/delete")
    public String deleteSlot(@PathVariable Long id, Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        try {
            tutorScheduleService.deleteSlot(id, tutor.getId());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/tutor/schedule";
    }

    @GetMapping("/requests")
    public String incomingRequests(@RequestParam(value = "status", required = false) RequestStatus status, Model model) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrNull();
        boolean isAdminView = tutor == null;
        List<TutoringRequest> requests = isAdminView
                ? tutorRequestService.findAll(status)
                : tutorRequestService.findIncoming(tutor.getId(), status);
        model.addAttribute("requests", requests);
        model.addAttribute("status", status);
        model.addAttribute("isAdminView", isAdminView);
        return "tutor/requests";
    }

    @PostMapping("/requests/{id}/confirm")
    public String confirm(@PathVariable Long id) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        tutorRequestService.changeStatus(tutor.getId(), id, RequestStatus.CONFIRMED);
        return "redirect:/tutor/requests";
    }

    @PostMapping("/requests/{id}/reject")
    public String reject(@PathVariable Long id) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        tutorRequestService.changeStatus(tutor.getId(), id, RequestStatus.CANCELLED);
        return "redirect:/tutor/requests";
    }

    @PostMapping("/requests/{id}/done")
    public String done(@PathVariable Long id) {
        TutorProfile tutor = currentUserService.getCurrentTutorOrThrow();
        tutorRequestService.changeStatus(tutor.getId(), id, RequestStatus.DONE);
        return "redirect:/tutor/requests";
    }
}
