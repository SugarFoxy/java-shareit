package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingServiceDb;
import ru.practicum.shareit.booking.state.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingServiceDb bookingService;

    @Autowired
    public BookingController(@Qualifier("bookingServiceDb") BookingServiceDb bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingOutputDto createBooking(
            @Valid @RequestBody BookingInputDto bookingInputDto,
            @RequestHeader("X-Sharer-User-Id") @Nullable Long userId
    ) {
        return bookingService.createBooking(bookingInputDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingOutputDto approve(
            @PathVariable Long bookingId,
            @RequestParam @NotNull Boolean approved,
            @RequestHeader("X-Sharer-User-Id") @Nullable Long userId
    ) {
        return bookingService.updateApprove(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingOutputDto getBookingInfo(@PathVariable Long bookingId,
                                           @RequestHeader("X-Sharer-User-Id") @Nullable Long userId) {
        return bookingService.getBookingInfo(bookingId, userId);
    }

    @GetMapping
    public List<BookingOutputDto> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") @Nullable Long userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingState state
    ) {
        return bookingService.getAllBookings(userId, state);
    }

    @GetMapping("owner")
    public List<BookingOutputDto> getAllBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id")@Nullable Long userId,
            @RequestParam(defaultValue = "ALL", required = false) BookingState state
    ) {
        return bookingService.getAllBookingsForOwner(userId, state);
    }
}
