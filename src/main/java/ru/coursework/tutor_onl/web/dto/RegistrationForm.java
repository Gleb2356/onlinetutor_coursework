package ru.coursework.tutor_onl.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationForm {

    @NotBlank(message = "Введите email")
    @Email(message = "Некорректный email")
    private String email;

    @NotBlank(message = "Введите пароль")
    @Size(min = 6, message = "Минимальная длина пароля — 6 символов")
    private String password;

    @NotBlank(message = "Укажите ФИО")
    private String fullName;

    private String city;

    @Size(max = 255, message = "Цель не должна превышать 255 символов")
    private String goal;
}
