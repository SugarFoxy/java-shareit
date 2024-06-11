package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperTest {
    private User user = User.builder().id(1L).name("user").email("email@mail.ru").build();
    private ItemRequest request = ItemRequest.builder()
            .id(1L)
            .requestor(new User())
            .created(LocalDateTime.now())
            .description("requestDesc")
            .build();
    private Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .owner(user)
            .request(request)
            .build();
    private List<CommentDto> comments = List.of(CommentDto.builder()
            .text("Фигня эти ваши тесты")
            .id(1L)
            .authorName("Гоша")
            .created(LocalDateTime.now())
            .build());
    private DateBookingDto bookingDtoLast = DateBookingDto.builder()
            .id(1L)
            .end(LocalDateTime.of(2022, 2, 2, 1, 1))
            .start(LocalDateTime.of(2022, 1, 1, 1, 1))
            .build();
    private DateBookingDto bookingDtoNext = DateBookingDto.builder()
            .id(2L)
            .end(LocalDateTime.of(2024, 2, 2, 1, 1))
            .start(LocalDateTime.of(2024, 1, 1, 1, 1))
            .build();

    @Test
    void toItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(user, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
        assertNull(itemDto.getComments());
    }

    @Test
    void testToItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item, comments);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(user, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
        assertEquals(comments, itemDto.getComments());
        assertNull(itemDto.getLastBooking());
        assertNull(itemDto.getNextBooking());
    }

    @Test
    void testToItemDto1() {
        ItemDto itemDto = ItemMapper.toItemDto(item, comments, bookingDtoLast, bookingDtoNext);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(user, itemDto.getOwner());
        assertEquals(1L, itemDto.getRequestId());
        assertEquals(comments, itemDto.getComments());
        assertEquals(bookingDtoLast, itemDto.getLastBooking());
        assertEquals(bookingDtoNext, itemDto.getNextBooking());
    }

    @Test
    void toItem() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .owner(user)
                .requestId(request.getId())
                .nextBooking(bookingDtoNext)
                .lastBooking(bookingDtoLast)
                .name("name")
                .comments(comments)
                .description("desc")
                .available(true)
                .build();

        Item item = ItemMapper.toItem(itemDto, request);

        assertEquals(this.item, item);
    }

    @Test
    void toItemForRequestDto() {
        ItemForRequestDto itemDto = ItemMapper.toItemForRequestDto(item);

        assertEquals(1L, itemDto.getId());
        assertEquals("name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
    }
}