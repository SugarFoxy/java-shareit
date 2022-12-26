package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.groups.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ItemDto {
    private Integer id;
    @NotBlank(message = "Название не может быть пустым",groups = Create.class)
    private String name;
    @NotBlank(message = "Описание не может быть пустым",groups = Create.class)
    private String description;
    @NotNull(message = "Статус не может отсутствовать",groups = Create.class)
    private Boolean available;
    private Integer owner;
}
