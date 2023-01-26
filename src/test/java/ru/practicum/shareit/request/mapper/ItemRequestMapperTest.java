package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperTest {
    private final User requestor = User.builder()
            .id(1L)
            .email("user@mail.ru")
            .name("name")
            .build();
    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .created(LocalDateTime.now())
            .description("descRequest")
            .requestor(requestor)
            .build();
    User owner = User.builder().id(2L).name("user").email("email@mail.ru").build();
    Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("desc")
            .available(true)
            .owner(owner)
            .request(request)
            .build();

    List<ItemForRequestDto> items = Stream.of(item)
            .map(ItemMapper::toItemForRequestDto).collect(Collectors.toList());

    @Test
    void toItemRequestDto() {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request);

        assertEquals(request.getId(),requestDto.getId());
        assertEquals(request.getDescription(),requestDto.getDescription());
        assertEquals(request.getCreated(),requestDto.getCreated());
        assertNull(requestDto.getItems());
    }

    @Test
    void toItemRequestDto2() {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(request, items);

        assertEquals(request.getId(),requestDto.getId());
        assertEquals(request.getDescription(),requestDto.getDescription());
        assertEquals(request.getCreated(),requestDto.getCreated());
        assertEquals(items,requestDto.getItems());
    }

    @Test
    void toItemRequest() {
        ItemRequestDto requestDto = new ItemRequestDto(1L,"descRequest",request.getCreated(),items);

        ItemRequest actualRequest = ItemRequestMapper.toItemRequest(requestDto,requestor);
        assertEquals(request,actualRequest);
    }
}