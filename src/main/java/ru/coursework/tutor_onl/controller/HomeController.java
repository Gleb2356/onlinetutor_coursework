package ru.coursework.tutor_onl.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("popularSubjects", List.of(
                "Математика",
                "Английский язык",
                "Физика",
                "Информатика",
                "Химия",
                "Русский язык"
        ));
        return "index";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }
}
