package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.booking.model.Booking;

public class BookingMapper {
    public  static BookingOutputDto toBookingDto(Booking booking){
        return  BookingOutputDto.builder()
                .id(booking.getId())
                .end(booking.getEnd())
                .start(booking.getStart())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getStatus())
                .build();
    }

    public static Booking toBooking(BookingOutputDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .end(bookingDto.getEnd())
                .start(bookingDto.getStart())
                .booker(bookingDto.getBooker())
                .item(bookingDto.getItem())
                .status(bookingDto.getStatus())
                .build();
    }

    public static DateBookingDto toDateBookingDto(Booking booking) {
        return DateBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .build();
    }
}
