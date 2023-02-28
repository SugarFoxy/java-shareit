package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.NotNull;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private final RequestClient requestClient;

    @GetMapping
    public ResponseEntity<Object> getYourRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("Запрос на получение своих запросов");
        return requestClient.getRequestsByUser(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                    @Validated @RequestBody ItemRequestDto requestDto) {
        log.info("Запрос на добавление запроса {}", requestDto);
        return requestClient.addRequest(requestDto, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                               @RequestParam(defaultValue = "0") Integer from,
                                               @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос на получение всех запросов");
        return requestClient.getAllRequests(userId,from,size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
                                                @PathVariable Long requestId) {
        log.info("Запрос на получение запроса {}", requestId);
        return requestClient.getRequestById(requestId,userId);
    }


}
