package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto addRequest(Long userId, ItemRequestDto requestDto);

    ItemRequestDto getRequestById(Long requestId, Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size);

    List<ItemRequestDto> getRequestsByUser(Long userId);
}
