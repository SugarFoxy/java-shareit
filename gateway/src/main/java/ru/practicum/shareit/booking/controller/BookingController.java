package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(
            @Valid @RequestBody BookingInputDto bookingInputDto,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("Запрос на создание бронирования - {}", bookingInputDto);
        return bookingClient.createBooking(userId, bookingInputDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(
            @PathVariable Long bookingId,
            @RequestParam @NotNull Boolean approved,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("Запрос на изменение утверждения бронирования - {}", approved);
        return bookingClient.updateApprove(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingInfo(@PathVariable Long bookingId,
                                                 @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("Запрос на информацию бронирования - {}", bookingId);
        return bookingClient.getBookingInfo(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookings(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        log.info("Запрос на получение всех бронирований");
        return bookingClient.getAllBookings(userId, state, from, size);
    }

    @GetMapping("owner")
    public ResponseEntity<Object> getAllBookingsForOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL", required = false) String state,
            @RequestParam(required = false) Integer from,
            @RequestParam(required = false) Integer size) {
        log.info("Запрос на получение всех бронирований пользователя - {}", userId);
        return bookingClient.getAllBookingsForOwner(userId, state, from, size);
    }
}
