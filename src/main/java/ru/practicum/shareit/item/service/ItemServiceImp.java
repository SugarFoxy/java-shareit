package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImp implements ItemService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public ItemServiceImp(@Qualifier("userStorageImp") UserStorage userStorage,
                          @Qualifier("itemStorageImp") ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public List<ItemDto> getItemsByUser(Integer userId) {
        userStorage.checkUserAvailability("найти", userId);
        return itemStorage.getItemsByUser(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemByText(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemStorage.getItemByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId, Integer userId) {
        userStorage.checkUserAvailability("найти", userId);
        Item item = itemStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto creatItem(Integer userId, ItemDto itemDto) {
        userStorage.checkUserAvailability("найти", userId);
        itemDto.setOwner(userId);
        Item item = itemStorage.addItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto updateItem(Integer userId, ItemDto itemDto, Integer itemId) {
        userStorage.checkUserAvailability("найти", userId);
        itemDto.setOwner(userId);
        itemDto.setId(itemId);
        Item item = itemStorage.updateItem(ItemMapper.toItem(itemDto));
        return ItemMapper.toItemDto(item);
    }
}
