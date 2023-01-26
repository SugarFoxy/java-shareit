package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutputDtoTest {

    @Autowired
    private JacksonTester<BookingOutputDto> json;

    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @Test
    void testBookingOutputDto() throws Exception {
        BookingOutputDto bookingOutputDto = new BookingOutputDto(
                1L,
                START,
                END,
                createItem(),
                createUser(),
                BookingStatus.APPROVED
        );

        JsonContent<BookingOutputDto> result = json.write(bookingOutputDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isNotBlank();
        assertThat(result).extractingJsonPathStringValue("$.end").isNotBlank();
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }

    private Item createItem() {
        return new Item(
                1L,
                "My item",
                "My item Description",
                true,
                createUser(),
                null
        );
    }

    private User createUser() {
        return new User(
                1L,
                "John Doe",
                "john.doe@mail.com"
        );
    }
}