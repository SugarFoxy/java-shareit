package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.groups.Create;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                 @RequestParam(required = false) Integer from,
                                                 @RequestParam(required = false) Integer size) {
        log.info("Запрос на получение вещей пользователя - {}", userId);
        return itemClient.getItemsByUser(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItemByText(@RequestParam String text,
                                       @RequestParam(required = false) Integer from,
                                       @RequestParam(required = false) Integer size) {
        log.info("Запрос на поиск вещи по названию или описанию: {}", text);
        return itemClient.getItemByText(text, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                               @PathVariable Long itemId) {
        log.info("Запрос на получение вещи по ID - {}", itemId);
        return itemClient.getItemById(itemId, userId);
    }

    @PostMapping
    public ResponseEntity<Object> creatItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                             @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Запрос на создание вещи - {}", itemDto);
        return itemClient.creatItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                              @Validated @RequestBody ItemDto itemDto,
                              @PathVariable @NotNull Long itemId) {
        log.info("Запрос на изменение вещи - {}", itemDto);
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(
            @PathVariable Long itemId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long authorId,
            @Validated @RequestBody CommentDto commentDto) {
        log.info("Запрос на добавление коментария - {}", commentDto);
        return itemClient.addComment(itemId, authorId, commentDto);
    }
}
