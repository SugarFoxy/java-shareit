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
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.OtherDataException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.state.BookingState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BookingServiceDbTest {
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

    private Item item;

    private User booker;

    private User owner;

    private User trespasser;

    private Booking booking;

    private BookingInputDto bookingInputDto;

    private final BookingInputDto input = new BookingInputDto(2L, START, END);

    @BeforeEach
    public void before() {
        owner = User.builder().id(1L).name("name").email("email.@mail.ru").build();
        booker = User.builder().id(2L).name("name2").email("email2.@mail.ru").build();
        trespasser = User.builder().id(3L).name("name3").email("email3.@mail.ru").build();
        item = Item.builder()
                .id(2L)
                .available(true)
                .owner(owner)
                .description("desc")
                .name("name")
                .request(null)
                .build();
        booking = Booking.builder()
                .id(3L)
                .booker(booker)
                .end(END)
                .start(START)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
        bookingInputDto = new BookingInputDto();
        when(bookingRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        when(itemRepository.getItemOwner(eq(2L))).thenReturn(owner);
    }

    @Test
    void createBooking_whenStartAfterNow_thenThrowException() {
        bookingInputDto.setEnd(LocalDateTime.of(2023, 1, 1, 1, 1));
        bookingInputDto.setStart(LocalDateTime.of(2021, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenEndAfterNow_thenThrowException() {
        bookingInputDto.setStart(LocalDateTime.of(2025, 1, 1, 1, 1));
        bookingInputDto.setEnd(LocalDateTime.of(2024, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenStartBeforeEnd_thenThrowException() {
        bookingInputDto.setStart(LocalDateTime.of(2022, 1, 1, 1, 1));
        bookingInputDto.setEnd(LocalDateTime.of(2021, 1, 1, 1, 1));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(bookingInputDto, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> bookingServiceDb.createBooking(input, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenItemNotAvailable_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder().available(false).build()));

        assertThrows(
                InvalidRequestException.class,
                () -> bookingServiceDb.createBooking(input, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenOwnerItem_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(
                OtherDataException.class,
                () -> bookingServiceDb.createBooking(input, 1L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenUserNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                MissingObjectException.class,
                () -> bookingServiceDb.createBooking(input, 2L));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createBooking_whenCorrect_thenSave() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(Item.builder()
                .available(true)
                .owner(User.builder()
                        .id(1L)
                        .build())
                .build()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        bookingServiceDb.createBooking(input, 2L);

        verify(bookingRepository).save(any());
    }

    @Test
    void updateApprove_whenBookingNotFound_thenThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.updateApprove(1L, true, 1L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateApprove_whenBookerNotFound_thenThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.updateApprove(1L, true, 1L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateApprove_whenNotOwnerItem_thenThrowException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(booker));

        assertThrows(OtherDataException.class,
                () -> bookingServiceDb.updateApprove(1L, true, 2L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateApprove_whenStatusNotWaiting_thenThrowException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));

        assertThrows(InvalidRequestException.class,
                () -> bookingServiceDb.updateApprove(3L, true, 1L));

        verify(bookingRepository, never()).save(any());
    }

    @Test
    void updateApprove_whenBookingSetStatusApproved_thenReturnedStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));

        BookingOutputDto result = bookingServiceDb.updateApprove(3L, true, 1L);

        verify(bookingRepository).save(any());

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    void updateApprove_whenBookingSetStatusRejected_thenReturnedStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));

        BookingOutputDto result = bookingServiceDb.updateApprove(3L, false, 1L);

        verify(bookingRepository).save(any());

        assertEquals(BookingStatus.REJECTED, result.getStatus());
    }

    @Test
    void updateApprove_whenBookingCorrect_thenUpdateOnlyStatus() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.findById(any())).thenReturn(Optional.of(owner));

        BookingOutputDto result = bookingServiceDb.updateApprove(3L, true, 1L);

        verify(bookingRepository).save(any());

        assertEquals(BookingStatus.APPROVED, result.getStatus());
        assertEquals(booking.getId(), result.getId());
        assertEquals(booking.getItem(), result.getItem());
        assertEquals(booking.getEnd(), result.getEnd());
        assertEquals(booking.getStart(), result.getStart());
        assertEquals(booking.getBooker(), result.getBooker());
    }

    @Test
    void getBookingInfo_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.getBookingInfo(3L, 4L));
    }

    @Test
    void getBookingInfo_whenBookingNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.getBookingInfo(3L, 1L));
    }

    @Test
    void getBookingInfo_whenNotOwnerAndBooker_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(trespasser));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        assertThrows(OtherDataException.class,
                () -> bookingServiceDb.getBookingInfo(3L, 1L));
    }

    @Test
    void getBookingInfo_whenOwnerRequest_thenReturnedInfo() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingOutputDto result = bookingServiceDb.getBookingInfo(3L, 1L);

        assertEquals(BookingMapper.toBookingDto(booking), result);
    }

    @Test
    void getBookingInfo_whenBookerRequest_thenReturnedInfo() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingOutputDto result = bookingServiceDb.getBookingInfo(3L, 1L);

        assertEquals(BookingMapper.toBookingDto(booking), result);
    }

    @Test
    void getAllBookings_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.getAllBookings(3L, ALL, null, null));
    }

    @Test
    void getAllBookings_whenStateALL_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker(any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, ALL, null, null);

        verify(bookingRepository).findByBooker(any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookings_whenStateCURRENT_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByBooker(any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, CURRENT, null, null);

        verify(bookingRepository).findCurrentByBooker(any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookings_whenStatePAST_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerAndEndIsBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, PAST, null, null);

        verify(bookingRepository).findByBookerAndEndIsBefore(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookings_whenStateFUTURE_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerAndStartIsAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, FUTURE, null, null);

        verify(bookingRepository).findByBookerAndStartIsAfter(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookings_whenStateWAITING_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, WAITING, null, null);

        verify(bookingRepository).findByBookerAndStatus(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookings_whenStateREJECTED_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBookerAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookings(2L, REJECTED, null, null);

        verify(bookingRepository).findByBookerAndStatus(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookingsForOwner_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> bookingServiceDb.getAllBookingsForOwner(3L, ALL, null, null));
    }

    @Test
    void getAllBookingsForOwner_whenStateALL_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByOwner(any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, ALL, null, null);

        verify(bookingRepository).findByOwner(any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookingsForOwner_whenStateCURRENT_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findCurrentByOwner(any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, CURRENT, null, null);

        verify(bookingRepository).findCurrentByOwner(any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookingsForOwner_whenStatePAST_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByOwnerAndEndIsBefore(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, PAST, null, null);

        verify(bookingRepository).findByOwnerAndEndIsBefore(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookingsForOwner_whenStateFUTURE_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByOwnerAndStartIsAfter(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, FUTURE, null, null);

        verify(bookingRepository).findByOwnerAndStartIsAfter(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }

    @Test
    void getAllBookingsForOwner_whenStateWAITING_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByOwnerAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, WAITING, null, null);

        verify(bookingRepository).findByOwnerAndStatus(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }


    @Test
    void getAllBookingsForOwner_whenStateREJECTED_thenReturnedListBookingDto() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByOwnerAndStatus(any(), any(), any())).thenReturn(List.of(booking));

        List<BookingOutputDto> result = bookingServiceDb.getAllBookingsForOwner(2L, REJECTED, null, null);

        verify(bookingRepository).findByOwnerAndStatus(any(), any(), any());
        assertFalse(result.isEmpty());
        assertEquals(BookingMapper.toBookingDto(booking), result.get(0));
    }
}