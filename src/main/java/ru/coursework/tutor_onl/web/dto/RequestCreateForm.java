package ru.coursework.tutor_onl.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestCreateForm {

    @NotNull
    private Long slotId;

    @Min(value = 30, message = "Длительность не менее 30 минут")
    private Integer durationMinutes;

    private String comment;
}
