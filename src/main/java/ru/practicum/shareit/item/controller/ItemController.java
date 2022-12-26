package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.groups.Create;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(@Qualifier("itemServiceImp") ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Integer userId,
                               @PathVariable Integer itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto creatItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                             @Validated(Create.class) @RequestBody ItemDto itemDto
    ) {
        return itemService.creatItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Integer userId,
                              @Validated @RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

}
