package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.User;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jtItemDto;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    public void itemDtoJsonTest() throws IOException {
        ItemDto itemDto = new ItemDto(1L,
                "Название",
                "Описание",
                true,
                1L,
                new User(1L,"name","email@mail.ru"),
                new ArrayList<>(),
                null,
                null);

        JsonContent<ItemDto> itemDtoResult = jtItemDto.write(itemDto);

        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.id").isEqualTo(itemDto.getId().intValue());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(itemDtoResult).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(itemDtoResult).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.owner.id").isEqualTo(1);
        assertThat(itemDtoResult).extractingJsonPathNumberValue("$.requestId").isEqualTo(itemDto.getRequestId().intValue());
        assertThat(itemDtoResult).extractingJsonPathValue("$.lastBooking").isNull();
        assertThat(itemDtoResult).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(itemDtoResult).extractingJsonPathArrayValue("$.comments").isEqualTo(itemDto.getComments());
    }
}