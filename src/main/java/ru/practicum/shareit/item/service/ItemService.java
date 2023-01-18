package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUser(Long userId);

    List<ItemDto> getItemByText(String text);

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto creatItem(Long userId, ItemDto itemDto);

    ItemDto updateItem(Long userId, ItemDto itemDto, Long itemId);
}
