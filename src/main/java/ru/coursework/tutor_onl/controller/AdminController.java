package ru.coursework.tutor_onl.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.coursework.tutor_onl.model.Review;
import ru.coursework.tutor_onl.model.RoleName;
import ru.coursework.tutor_onl.model.Subject;
import ru.coursework.tutor_onl.model.User;
import ru.coursework.tutor_onl.service.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminUserService adminUserService;
    private final AdminSubjectService adminSubjectService;
    private final AdminReviewService adminReviewService;
    private final AdminReportService adminReportService;

    @GetMapping
    public String adminHome() {
        return "admin/index";
    }

    @GetMapping("/users")
    public String users(@RequestParam(value = "q", required = false) String query, Model model) {
        List<User> users = adminUserService.listUsers(query);
        model.addAttribute("users", users);
        model.addAttribute("roles", RoleName.values());
        model.addAttribute("q", query);
        return "admin/users";
    }

    @PostMapping("/users/{id}/role")
    public String changeRole(@PathVariable Long id, @RequestParam RoleName role) {
        adminUserService.changeRole(id, role);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        adminUserService.toggleBlocked(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/subjects")
    public String subjects(@RequestParam(value = "q", required = false) String query, Model model) {
        List<Subject> subjects = adminSubjectService.list(query);
        model.addAttribute("subjects", subjects);
        model.addAttribute("q", query);
        return "admin/subjects";
    }

    @PostMapping("/subjects")
    public String createSubject(@RequestParam String name, @RequestParam(required = false) String description, Model model) {
        try {
            adminSubjectService.create(name, description);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/{id}")
    public String updateSubject(@PathVariable Long id, @RequestParam String name, @RequestParam(required = false) String description) {
        adminSubjectService.update(id, name, description);
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/{id}/delete")
    public String deleteSubject(@PathVariable Long id) {
        adminSubjectService.delete(id);
        return "redirect:/admin/subjects";
    }

    @GetMapping("/reviews")
    public String reviews(Model model) {
        List<Review> reviews = adminReviewService.listAll();
        model.addAttribute("reviews", reviews);
        return "admin/reviews";
    }

    @PostMapping("/reviews/{id}/hide")
    public String hideReview(@PathVariable Long id) {
        adminReviewService.hide(id);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/{id}/unhide")
    public String unhideReview(@PathVariable Long id) {
        adminReviewService.unhide(id);
        return "redirect:/admin/reviews";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        adminReviewService.delete(id);
        return "redirect:/admin/reviews";
    }

    @GetMapping("/reports")
    public String reports(Model model) {
        Map<String, Object> metrics = adminReportService.metrics();
        model.addAttribute("metrics", metrics);
        return "admin/reports";
    }
}
