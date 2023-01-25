package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.dto.DateBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public class ItemMapper {
    public static ItemDto toItemDto(
            Item item,
            List<CommentDto> comments,
            DateBookingDto lastBooking,
            DateBookingDto nextBooking
    ) {
        ItemRequest request = item.getRequest();
        Long requestId = request != null ? request.getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(requestId)
                .comments(comments)
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public static ItemDto toItemDto(Item item) {
        ItemRequest request = item.getRequest();
        Long requestId = request != null ? request.getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .owner(item.getOwner())
                .build();
    }

    public static ItemDto toItemDto(Item item, List<CommentDto> comments) {
        ItemRequest request = item.getRequest();
        Long requestId = request != null ? request.getId() : null;
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner())
                .requestId(requestId)
                .comments(comments)
                .build();
    }

    public static Item toItem(ItemDto itemDto, ItemRequest request) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(itemDto.getOwner())
                .request(request)
                .build();
    }

    public static ItemForRequestDto toItemForRequestDto(Item item) {
        return ItemForRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest().getId())
                .build();
    }
}
