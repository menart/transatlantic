package express.atc.backend.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

import static express.atc.backend.Constants.EMAIL_NOT_VALID;
import static express.atc.backend.Constants.NOT_VALID_SIZE;

public record DeliveryDto(
        @Schema(description = "Фамилия Имя Отчество")
        @NotNull(message = "ФИО: поле не может быть пустым")
        String fullName,
        @Schema(description = "Адрес электронной почты (максимальная длина поля 150)")
        @NotNull(message = "Адрес электронной почты: поле не может быть пустым")
        @Size(min = 5, max = 150, message = "Адрес электронной почты: " + NOT_VALID_SIZE)
        @Email(message = EMAIL_NOT_VALID)
        String email,
        @Schema(description = "Наименование груза")
        @NotNull(message = "Наименование груза: поле не может быть пустым")
        String title,
        @Schema(description = "Вес груза")
        @NotNull(message = "Вес груза: поле не может быть пустым")
        @Min(value = 1L, message = "Вес груза не может быть меньше 1")
        Integer weight,
        @Schema(description = "Количество мест")
        @NotNull(message = "Количество мест: поле не может быть пустым")
        @Min(value = 1L, message = "Количество мест не может быть меньше 1")
        Integer count,
        @Schema(description = "Габариты мест")
        @NotNull(message = "Габариты мест: поле не может быть пустым")
        @Size(min = 1, message = "Количество мест не может быть меньше 1")
        List<DeliveryItemDto> items,
        @Schema(description = "Комментарий")
        @NotNull(message = "Комментарий: поле не может быть пустым")
        String description
) {
}
