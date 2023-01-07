package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getItemsByUser(Integer userId);

    List<ItemDto> getItemByText(String text);

    ItemDto getItemById(Integer itemId, Integer userId);

    ItemDto creatItem(Integer userId, ItemDto itemDto);

    ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId);
}
