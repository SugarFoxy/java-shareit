package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

public class BookingMapperTest {
    private final User owner = User.builder().id(2L).build();
    private final ItemRequest request = ItemRequest.builder().id(1L).build();

    private final Booking booking = new Booking(
            1L,
            LocalDateTime.of(2024, 1, 1, 1, 1, 1),
            LocalDateTime.of(2024, 1, 2, 1, 1, 1),
            new Item(1L, "Корректный предмет", "Корректное описание", true, owner, request),
            new User(1L, "user", "user@gmail.com"),
            WAITING
    );

    @Test
    public void toBookingDtoTest() {
        BookingOutputDto bookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
        assertEquals(booking.getStatus(), bookingDto.getStatus());
    }

    @Test
    public void toDateBookingDtoTest() {
        DateBookingDto bookingDto = BookingMapper.toDateBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }


    @Test
    public void toBookingTest() {
        BookingOutputDto bookingDto = new BookingOutputDto(
                1L,
                LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                LocalDateTime.of(2024, 1, 2, 1, 1, 1),
                new Item(),
                new User(),
                WAITING
        );

        Booking newBooking = BookingMapper.toBooking(bookingDto);

        assertEquals(bookingDto.getId(), newBooking.getId());
        assertEquals(bookingDto.getStart(), newBooking.getStart());
        assertEquals(bookingDto.getEnd(), newBooking.getEnd());
        assertEquals(new Item(), newBooking.getItem());
        assertEquals(new User(), newBooking.getBooker());
        assertEquals(bookingDto.getStatus(), newBooking.getStatus());
    }
}