package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImp implements ItemStorage {
    private final Map<Integer, Item> items = new HashMap<>();
    int id = 1;

    @Override
    public List<Item> getItemsByUser(Integer userId) {
        return items.values().stream()
                .filter(item -> Objects.equals(item.getOwner(), userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getItemByText(String text) {
        return items.values().stream()
                .filter(item -> mergeNameAndDesc(item.getId()).lastIndexOf(text.toLowerCase()) > -1)
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Integer itemId) {
        checkAvailability("найти", itemId);
        return items.get(itemId);
    }

    @Override
    public Item addItem(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        checkAvailability("изменить", item.getId());
        checkOwner(item.getId(), item.getOwner());
        Item updateItem = items.get(item.getId());
        if (item.getName() != null && !item.getName().isBlank())
            updateItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            updateItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            updateItem.setAvailable(item.getAvailable());
        return updateItem;
    }

    private int getId() {
        return id++;
    }

    private void checkAvailability(String operation, int id) {
        String massage = String.format("Невозможно %s. Вещь не нвйдена!", operation);
        if (!items.containsKey(id))
            throw new MissingObjectException(massage);
    }

    private void checkOwner(int itemId, int userId) {
        if (items.get(itemId).getOwner() != userId)
            throw new MissingObjectException("Вещь принадлежит другому пользователю!");
    }

    private String mergeNameAndDesc(int itemId) {
        Item item = items.get(itemId);
        String merge = item.getName() + " " + item.getDescription();
        return merge.toLowerCase();
    }
}
