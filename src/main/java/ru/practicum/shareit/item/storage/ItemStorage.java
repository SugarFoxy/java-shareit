package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getItemsByUser(Integer userId);

    List<Item> getItemByText(String text);

    Item getItemById(Integer itemId);

    Item addItem(Item item);

    Item updateItem(Item item);
}
