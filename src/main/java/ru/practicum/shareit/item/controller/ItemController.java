package ru.practicum.shareit.item.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.groups.Create;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final CommentService commentService;

    @Autowired
    public ItemController(@Qualifier("itemServiceDb") ItemService itemService,
                          @Qualifier("itemServiceDb") CommentService commentService) {
        this.itemService = itemService;
        this.commentService = commentService;
    }

    @GetMapping
    public List<ItemDto> getItemsByUser(@RequestHeader("X-Sharer-User-Id") @Nullable Long userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemByText(@RequestParam String text) {
        return itemService.getItemByText(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") @Nullable Long userId,
                               @PathVariable Long itemId) {
        return itemService.getItemById(itemId, userId);
    }

    @PostMapping
    public ItemDto creatItem(@RequestHeader("X-Sharer-User-Id") @Nullable Long userId,
                             @Validated(Create.class) @RequestBody ItemDto itemDto
    ) {
        return itemService.creatItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @Nullable Long userId,
                              @Validated @RequestBody ItemDto itemDto,
                              @PathVariable @NotNull Long itemId) {
        return itemService.updateItem(userId, itemDto, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") @Nullable Long authorId,
            @Validated @RequestBody CommentDto commentDto
    ) {
        return commentService.addComment(itemId, authorId, commentDto);
    }

}
