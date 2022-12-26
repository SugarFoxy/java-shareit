package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.groups.Create;
import ru.practicum.shareit.validation.groups.Update;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    @NotBlank(message = "Имя не может быть пустым",groups = {Create.class})
    private String name;
    @Email(message = "Некорректный email",groups = {Create.class, Update.class})
    @NotBlank(message = "email не может быть пустым",groups = {Create.class})
    private String email;
}
