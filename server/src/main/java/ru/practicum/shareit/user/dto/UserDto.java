package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import ru.practicum.shareit.validation.groups.Create;
import ru.practicum.shareit.validation.groups.Update;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Long id;
    @NotBlank(message = "Имя не может быть пустым", groups = {Create.class})
    private String name;
    @Email(message = "Некорректный email", groups = {Create.class, Update.class})
    @NotBlank(message = "email не может быть пустым", groups = {Create.class})
    private String email;
}
