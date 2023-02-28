package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.item.dto.ItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto addRequest(Long userId, ItemRequestDto requestDto) {
        requestDto.setCreated(LocalDateTime.now());
        return ItemRequestMapper.toItemRequestDto(requestRepository.save(ItemRequestMapper.toItemRequest(requestDto, getUser(userId))));
    }

    @Override
    public ItemRequestDto getRequestById(Long requestId, Long userId) {
        getUser(userId); //для проверки наличия
        ItemRequest request = getRequest(requestId);
        return ItemRequestMapper.toItemRequestDto(request, getAnswers(request));
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        return requestRepository.findByRequestorNot(getUser(userId),
                        CustomPageRequest.create(from, size, Sort.by("created").descending())).stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, getAnswers(request)))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getRequestsByUser(Long userId) {
        return requestRepository.findByRequestor(getUser(userId)).stream()
                .map(request -> ItemRequestMapper.toItemRequestDto(request, getAnswers(request)))
                .sorted(Comparator.comparing(ItemRequestDto::getCreated, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new MissingObjectException("Невозможно найти. Пользователь отсутствует!"));
    }

    private ItemRequest getRequest(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new MissingObjectException("Невозможно найти. Заорпос отсутствует!"));
    }

    private List<ItemForRequestDto> getAnswers(ItemRequest itemRequest) {
        return itemRepository.findByRequest(itemRequest).stream()
                .map(ItemMapper::toItemForRequestDto)
                .collect(Collectors.toList());
    }
}
