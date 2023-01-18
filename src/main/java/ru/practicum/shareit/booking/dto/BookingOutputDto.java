package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class BookingOutputDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus status;

    public BookingOutputDto(BookingInputDto bookingInputDto, Long id, Item item, User booker, BookingStatus status) {
        this.id = id;
        this.start = bookingInputDto.getStart();
        this.end = bookingInputDto.getEnd();
        this.item = item;
        this.booker = booker;
        this.status = status;
    }
}
