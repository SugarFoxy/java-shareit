package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.OtherDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceDbTest {

    //TODO доделать
    private static final long ITEM_ID = 111L;
    private static final long BOOKING_ID = 222L;
    private static final long BOOKER_ID = 123L;
    private static final long OWNER_ID = 321L;
    private static final long TRESPASSER_ID = 666L;

    private static final LocalDateTime START = LocalDateTime.of(2032, 9, 15, 9, 19);
    private static final LocalDateTime END = LocalDateTime.of(2033, 1, 1, 0, 0);

    @InjectMocks
    private BookingServiceDb bookingServiceDb;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Item item;
    @Mock
    private User booker;
    @Mock
    private User owner;
    @Mock
    private User trespasser;
    @Mock
    private Booking booking;

    private BookingInputDto bookingInputDto;

    private final BookingInputDto input = new BookingInputDto(ITEM_ID, START, END);

    @BeforeEach
    public void before() {
        bookingInputDto = new BookingInputDto();
        when(bookingRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        when(itemRepository.getItemOwner(eq(ITEM_ID))).thenReturn(owner);
    }

    @Test
    void createBooking_whenStartAfterNow_thenThrowException() {
        bookingInputDto.setEnd(LocalDateTime.of(2023, 1, 1, 1, 1));
        bookingInputDto.setStart(LocalDateTime.of(2021, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenEndAfterNow_thenThrowException() {
        bookingInputDto.setStart(LocalDateTime.of(2025, 1, 1, 1, 1));
        bookingInputDto.setEnd(LocalDateTime.of(2024, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenStartBeforeEnd_thenThrowException() {
        bookingInputDto.setStart(LocalDateTime.of(2022, 1, 1, 1, 1));
        bookingInputDto.setEnd(LocalDateTime.of(2021, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> bookingServiceDb.createBooking(input, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().available(false).build()));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(input, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenOwnerItem_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .available(true)
                .owner(User.builder()
                        .id(OWNER_ID)
                        .build())
                .build()));

        assertThrows(
                OtherDataException.class,
                () -> bookingServiceDb.createBooking(input, OWNER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .available(true)
                .owner(User.builder()
                        .id(OWNER_ID)
                        .build())
                .build()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                MissingObjectException.class,
                () -> bookingServiceDb.createBooking(input, BOOKER_ID));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenCorrect_thenSave() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .available(true)
                .owner(User.builder()
                        .id(OWNER_ID)
                        .build())
                .build()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        bookingServiceDb.createBooking(input,BOOKER_ID);

        verify(bookingRepository).save(any());
    }

    @Test
    void updateApprove() {
    }

    @Test
    void getBookingInfo() {
    }

    @Test
    void getAllBookings() {
    }

    @Test
    void getAllBookingsForOwner() {
    }
}