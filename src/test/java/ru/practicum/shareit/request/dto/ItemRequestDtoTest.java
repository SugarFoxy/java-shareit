package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemForRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class ItemRequestDtoTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;
    private static final LocalDateTime created = LocalDateTime.of(2032, 9, 15, 9, 19);

    private static final ItemForRequestDto item = ItemForRequestDto.builder()
            .id(1L)
            .available(true)
            .requestId(1L)
            .build();

    private static final List<ItemForRequestDto> items = List.of(item);

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDto requestDto = new ItemRequestDto(1L, "desc", created, items);

        JsonContent<ItemRequestDto> result = json.write(requestDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("desc");
        assertThat(result).extractingJsonPathStringValue("$.created").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
    }
}