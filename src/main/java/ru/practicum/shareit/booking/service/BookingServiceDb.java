package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.dto.comparator.BookingComparator;
import ru.practicum.shareit.booking.dto.model.Booking;
import ru.practicum.shareit.booking.dto.model.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.OtherDataException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookingServiceDb implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Autowired
    public BookingServiceDb(BookingRepository bookingRepository, UserRepository userRepository, ItemRepository itemRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    public BookingOutputDto createBooking(BookingInputDto bookingInputDto, Long userId) {
        isTheTimeCorrect(bookingInputDto);

        Item item = getItem(bookingInputDto.getItemId());

        if (!item.getAvailable()) {
            throw new InvalidRequestException("Вешь снята с аренды");
        }
        if (userId.equals(item.getOwner().getId())) {
            throw new OtherDataException("Пользователь не может забронировать свою вещь");
        }

        User user = getUser(userId);
        BookingOutputDto bookingOutputDto = new BookingOutputDto(bookingInputDto, null, item, user, BookingStatus.WAITING);
        log.info("Бронирование создано");
        return BookingMapper.toBookingDto(bookingRepository.save(BookingMapper.toBooking(bookingOutputDto)));
    }

    @Override
    public BookingOutputDto updateApprove(Long bookingId, Boolean approved, Long userId) {

        Booking booking = getBooking(bookingId);
        User user = getUser(userId);
        User owner = booking.getItem().getOwner();
        if (!user.equals(owner)) {
            throw new OtherDataException("Статус бронирования может изменить только владелец");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new InvalidRequestException("Статус бронирования можно изменить только во время его ожидания");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        log.info("Бронирование изменено");
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingOutputDto getBookingInfo(Long bookingId, Long userId) {
        User user = getUser(userId);
        Booking booking = getBooking(bookingId);
        User owner = booking.getItem().getOwner();
        User booker = booking.getBooker();
        boolean canGetInfo = owner.equals(user) || booker.equals(user);
        if (!canGetInfo) {
            throw new OtherDataException("Просматривать информацию о бронировании могут владелец или бронирующий");
        }
        log.info("Получен запрос на получение информации о бронировании");
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingOutputDto> getAllBookings(Long bookerId, BookingState state, Integer from, Integer size) {
        User booker = getUser(bookerId);
        Pageable pageable = CustomPageRequest.create(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return convertBookings(bookingRepository.findByBooker(booker, pageable));
            case CURRENT:
                return convertBookings(bookingRepository.findCurrentByBooker(booker, pageable));
            case PAST:
                return convertBookings(bookingRepository.findByBookerAndEndIsBefore(booker, LocalDateTime.now(), pageable));
            case FUTURE:
                return convertBookings(bookingRepository.findByBookerAndStartIsAfter(booker, LocalDateTime.now(), pageable));
            case WAITING:
                return convertBookings(bookingRepository.findByBookerAndStatus(booker, BookingStatus.WAITING, pageable));
            case REJECTED:
                return convertBookings(bookingRepository.findByBookerAndStatus(booker, BookingStatus.REJECTED, pageable));
        }

        throw new UnknownStateException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingOutputDto> getAllBookingsForOwner(Long ownerId, BookingState state, Integer from, Integer size) {
        User owner = getUser(ownerId);
        Pageable pageable = CustomPageRequest.create(from, size, Sort.by(Sort.Direction.DESC, "start"));

        switch (state) {
            case ALL:
                return convertBookings(bookingRepository.findByOwner(owner, pageable));
            case CURRENT:
                return convertBookings(bookingRepository.findCurrentByOwner(owner, pageable));
            case PAST:
                return convertBookings(bookingRepository.findByOwnerAndEndIsBefore(owner, LocalDateTime.now(), pageable));
            case FUTURE:
                return convertBookings(bookingRepository.findByOwnerAndStartIsAfter(owner, LocalDateTime.now(), pageable));
            case WAITING:
                return convertBookings(bookingRepository.findByOwnerAndStatus(owner, BookingStatus.WAITING, pageable));
            case REJECTED:
                return convertBookings(bookingRepository.findByOwnerAndStatus(owner, BookingStatus.REJECTED, pageable));
        }

        throw new IllegalArgumentException("Unknown state: UNSUPPORTED_STATUS");
    }

    private List<BookingOutputDto> convertBookings(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .sorted(new BookingComparator().reversed())
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MissingObjectException("Невозможно найти. Пользователь отсутствует!"));
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new MissingObjectException("Невозможно найти. Вещь отсутствует!"));
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new MissingObjectException("Невозможно найти. Бронь отсутствует!"));
    }

    private void isTheTimeCorrect(BookingInputDto bookingInputDto) {
        if (bookingInputDto.getStart().isBefore(LocalDateTime.now())
                || bookingInputDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Начало и конец бронирования не могу быть в прошлом");
        }
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart())) {
            throw new InvalidRequestException("Конец бронирования не может быть раньше начала");
        }
    }
}
