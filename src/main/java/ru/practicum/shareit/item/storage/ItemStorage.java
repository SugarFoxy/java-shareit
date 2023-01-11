package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItemsByUser(Long userId);

    List<Item> getItemByText(String text);

    Item getItemById(Long itemId);

    Item addItem(Item item);

    Item updateItem(Item item);
}
