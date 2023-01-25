package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ItemRequestServiceImplTest {

    @InjectMocks
    ItemRequestServiceImpl requestService;
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository requestRepository;
    ItemRequestDto itemRequestDto;
    ItemRequest request;
    Item item;
    User owner;
    User requestor;

    @BeforeEach
    void init() {

        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@mail.ru")
                .build();

        requestor = User.builder()
                .id(2L)
                .name("requestor")
                .email("requestor@mail.ru")
                .build();
        item = Item.builder()
                .id(1L)
                .available(true)
                .owner(owner)
                .name("name")
                .build();
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description("desc")
                .build();
        item.setRequest(ItemRequestMapper.toItemRequest(itemRequestDto, requestor));
        itemRequestDto.setItems(List.of(ItemMapper.toItemForRequestDto(item)));

        request = ItemRequest.builder()
                .id(2L)
                .created(LocalDateTime.now())
                .description("desc")
                .requestor(requestor)
                .build();

        when(requestRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void addRequest_whenRequestorNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class, () -> requestService.addRequest(2L, itemRequestDto));

        verify(requestRepository, never()).save(any());
    }

    @Test
    void addRequest_whenRequestCorrect_thenThrowException() {
        itemRequestDto.setItems(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));

        ItemRequestDto result = requestService.addRequest(2L, itemRequestDto);

        verify(requestRepository).save(any());
        assertNotNull(result);
        assertEquals(itemRequestDto, result);
    }

    @Test
    void getRequestById_whenRequestorNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class, () -> requestService.getRequestById(2L, 1L));

        verify(itemRepository, never()).findByRequest(any());
    }

    @Test
    void getRequestById_whenRequestNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class, () -> requestService.getRequestById(2L, 1L));

        verify(itemRepository, never()).findByRequest(any());
    }

    @Test
    void getRequestById_whenCorrect_thenReturnedRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(requestRepository.findById(any())).thenReturn(Optional.of(request));
        when(itemRepository.findByRequest(any())).thenReturn(List.of(item));

        ItemRequestDto result = requestService.getRequestById(2L, 1L);

        verify(itemRepository).findByRequest(any());
        ItemRequestDto collation = ItemRequestMapper.toItemRequestDto(request);
        collation.setItems(Stream.of(item).map(ItemMapper::toItemForRequestDto).collect(Collectors.toList()));
        assertEquals(collation, result);
    }

    @Test
    void getAllRequests_whenRequestorNotFound_thenThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class, () -> requestService.getAllRequests(2L, null, null));

        verify(itemRepository, never()).findByRequest(any());
    }

    @Test
    void getAllRequests_whenCorrect_thenGetList() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(requestRepository.findByRequestorNot(any(), any())).thenReturn(requestsInDb);

        List<ItemRequestDto> result = requestService.getAllRequests(2L, null, null);

        ArgumentCaptor<CustomPageRequest> captor = ArgumentCaptor.forClass(CustomPageRequest.class);
        verify(requestRepository).findByRequestorNot(any(), captor.capture());
        CustomPageRequest pageable = captor.getValue();
        assertEquals(6, result.size());
        assertEquals(0L, pageable.getOffset());
        assertEquals(Integer.MAX_VALUE, pageable.getPageSize());
    }

    @Test
    public void getAllRequests_whenFromAndSizeFound_thenGetList() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(requestRepository.findByRequestorNot(any(), any())).thenReturn(requestsInDb);
        List<ItemRequestDto> result = requestService.getAllRequests(2L, 1, 4);


        ArgumentCaptor<CustomPageRequest> captor = ArgumentCaptor.forClass(CustomPageRequest.class);
        verify(requestRepository).findByRequestorNot(any(), captor.capture());
        CustomPageRequest pageable = captor.getValue();
        assertFalse(result.isEmpty());
        assertEquals(1L, pageable.getOffset());
        assertEquals(4, pageable.getPageSize());
    }

    @Test
    void getRequestsByUser_whenUserNotFound_thenThrowException() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class, () -> requestService.getRequestsByUser(1L));

        verify(itemRepository, never()).findByRequest(any());
    }

    @Test
    void getRequestsByUser_whenCorrect_thenReturnedList() {
        List<ItemRequest> requestsInDb = new ArrayList<>();
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        requestsInDb.add(getMockItemRequest());
        when(userRepository.findById(any())).thenReturn(Optional.of(requestor));
        when(requestRepository.findByRequestor(any())).thenReturn(requestsInDb);
        when(itemRepository.findByRequest(any())).thenReturn(List.of(item));

        List<ItemRequestDto> result = requestService.getRequestsByUser(2L);

        verify(itemRepository, times(3)).findByRequest(any());

        assertEquals(requestsInDb.size(), result.size());
    }

    private ItemRequest getMockItemRequest() {
        ItemRequest mock = mock(ItemRequest.class);
        when(mock.getRequestor()).thenReturn(owner);
        return mock;
    }
}