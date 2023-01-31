package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.OtherDataException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class ItemServiceDbTest {

    private static final long OWNER_ID = 1;
    private static final long REQUESTOR_ID = 2;
    private static final long ITEM_ID = 1;
    private static final long REQUEST_ID = 1;

    @InjectMocks
    private ItemServiceDb itemServiceDb;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private User owner;
    @Mock
    private User requestor;
    @Mock
    private Item item;
    @Mock
    private ItemRequest request;

    private final ItemDto input = ItemDto.builder()
            .id(ITEM_ID)
            .name("name").description("desc")
            .available(true)
            .comments(new ArrayList<>())
            .owner(User.builder().id(1L).name("name").email("name@mail.ru").build())
            .lastBooking(null)
            .nextBooking(null)
            .requestId(null)
            .build();

    private final Item outRep = Item.builder()
            .id(ITEM_ID)
            .name("test").description("test")
            .owner(User.builder().id(1L).name("name").email("name@mail.ru").build())
            .available(false)
            .request(null)
            .build();

    @BeforeEach
    public void before() {
        when(itemRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
        when(itemRequestRepository.findById(eq(REQUEST_ID))).thenReturn(Optional.of(request));
        when(commentRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void getItemsByUser_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.getItemsByUser(OWNER_ID, null, null));
        verify(itemRepository, never()).findByOwner(any(), any());
    }

    @Test
    void getItemsByUser_whenCorrect_thenReturnedItems() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));

        List<ItemDto> result = itemServiceDb.getItemsByUser(OWNER_ID, null, null);

        verify(itemRepository).findByOwner(any(), any());
        assertEquals(new ArrayList<>(), result);
    }

    @Test
    void getItemsByUser_whenSizeNull_thenThrowException() {
        assertThrows(
                InvalidRequestException.class,
                () -> itemServiceDb.getItemsByUser(OWNER_ID, 0, null));
        verify(itemRepository, never()).findByOwner(any(), any());
    }

    @Test
    void getItemsByUser_whenFromNull_thenThrowException() {
        assertThrows(
                InvalidRequestException.class,
                () -> itemServiceDb.getItemsByUser(OWNER_ID, null, 1));
        verify(itemRepository, never()).findByOwner(any(), any());
    }

    @Test
    void getItemsByUser_whenFromNotCorrect_thenThrowException() {
        assertThrows(
                InvalidRequestException.class,
                () -> itemServiceDb.getItemsByUser(OWNER_ID, -1, 1));
        verify(itemRepository, never()).findByOwner(any(), any());
    }

    @Test
    void getItemsByUser_whenSizeNotCorrect_thenThrowException() {
        assertThrows(
                InvalidRequestException.class,
                () -> itemServiceDb.getItemsByUser(OWNER_ID, 0, 0));
        verify(itemRepository, never()).findByOwner(any(), any());
    }


    @Test
    void getItemByText_whenTextBlank_thenReturnedEmptyArray() {
        List<ItemDto> items = itemServiceDb.getItemByText("  ", null, null);
        verify(itemRepository, never()).search(any(), any());
        assertEquals(new ArrayList<>(), items);
    }

    @Test
    void getItemByText_whenCorrect_thenReturnedResultSearch() {
        itemServiceDb.getItemByText("TEXT123a", null, null);
        verify(itemRepository).search(eq("text123a"), any());
    }

    @Test
    void getItemById_whenCorrect_thenReturnedItemDto() {
        Item item1 = Item.builder()
                .id(1L)
                .available(true)
                .description("test")
                .name("test")
                .request(new ItemRequest())
                .owner(User.builder().id(1L).email("test@mail.ru").name("name").build())
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item1));

        itemServiceDb.getItemById(ITEM_ID, OWNER_ID);

        verify(itemRepository).findById(anyLong());
    }

    @Test
    void getItemById_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.getItemById(ITEM_ID, OWNER_ID));
    }

    @Test
    void getItemById_whenUserNotOwner_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(outRep));
        ItemDto result = itemServiceDb.getItemById(1L, 2L);

        assertNull(result.getLastBooking());
        assertNull(result.getNextBooking());
    }

    @Test
    void getItemById_whenUserOwner_thenThrowException() {
        Booking booking = Booking.builder()
                .id(1L)
                .status(BookingStatus.APPROVED)
                .item(item)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now())
                .booker(new User())
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(outRep));
        when(bookingRepository.findByItemAndEndIsBefore(any(), any())).thenReturn(List.of(booking));
        when(bookingRepository.findByItemAndStartIsAfter(any(), any())).thenReturn(List.of(booking));
        ItemDto result = itemServiceDb.getItemById(1L, 1L);

        assertEquals(BookingMapper.toDateBookingDto(booking), result.getLastBooking());
        assertEquals(BookingMapper.toDateBookingDto(booking), result.getNextBooking());
    }

    @Test
    void creatItem_whenCorrect_thenSave() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        itemServiceDb.creatItem(OWNER_ID, input);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(ITEM_ID, savedItem.getId());
        assertEquals(input.getName(), savedItem.getName());
        assertEquals(input.getDescription(), savedItem.getDescription());
        assertEquals(input.getAvailable(), savedItem.getAvailable());
        assertEquals(owner, savedItem.getOwner());
    }

    @Test
    void creatItem_whenOwnerNotFound_thenThrowException() {
        when(userRepository.findById(OWNER_ID)).thenReturn(Optional.empty());
        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.creatItem(OWNER_ID, input));
        verify(itemRepository, never()).save(any());
    }

    @Test
    public void creatItem_whenFoundRequestItem_thenSaveItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        input.setRequestId(REQUEST_ID);
        itemServiceDb.creatItem(OWNER_ID, input);

        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        verify(itemRepository).save(captor.capture());

        Item savedItem = captor.getValue();
        assertEquals(request, savedItem.getRequest());
    }

    @Test
    void updateItem_whenUserNotOwner_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.getItemOwner(anyLong())).thenReturn(User.builder().id(3L).build());

        assertThrows(
                OtherDataException.class,
                () -> itemServiceDb.updateItem(1L, input, ITEM_ID));

        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenItemNotfound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.creatItem(OWNER_ID, input));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenUserNotfound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.creatItem(OWNER_ID, input));
        verify(itemRepository, never()).save(any());
    }

    @Test
    void updateItem_whenCorrect_thenUpdate() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(outRep));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.getItemOwner(anyLong())).thenReturn(owner);

        ItemDto result = itemServiceDb.updateItem(OWNER_ID, input, ITEM_ID);

        verify(itemRepository).save(any());

        assertEquals(input, result);
    }

    @Test
    void addComment_whenItemNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.addComment(ITEM_ID, 1L, new CommentDto()));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenUserNotFound_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(
                MissingObjectException.class,
                () -> itemServiceDb.addComment(ITEM_ID, 1L, new CommentDto()));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenUserNotBooker_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(bookingRepository.findByBookerAndItem(any(), any())).thenReturn(new ArrayList<>());

        assertThrows(
                InvalidRequestException.class,
                () -> itemServiceDb.addComment(ITEM_ID, 1L, new CommentDto()));

        verify(commentRepository, never()).save(any());
    }

    @Test
    void addComment_whenCorrect_thenAddComment() {
        Booking booking = new Booking(362L, LocalDateTime.now(), LocalDateTime.now(), item, requestor,
                BookingStatus.APPROVED);
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(booking);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(bookingRepository.findByBookerAndItem(eq(requestor), eq(item))).thenReturn(bookingList);

        itemServiceDb.addComment(
                ITEM_ID,
                REQUESTOR_ID,
                CommentDto.builder()
                        .id(1L)
                        .text("commentText")
                        .authorName("authorName")
                        .build());

        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        Comment comment = captor.getValue();

        assertEquals("commentText", comment.getText());
    }
}