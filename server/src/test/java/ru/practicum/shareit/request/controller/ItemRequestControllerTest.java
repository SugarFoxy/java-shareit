package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService requestService;

    private ItemRequestDto requestDto;

    @BeforeEach
    void init() {
        requestDto = new ItemRequestDto();
    }

    @SneakyThrows
    @Test
    void getYourRequests_whenRequestCorrect_thenReturnedOk() {
        when(requestService.getRequestsByUser(anyLong())).thenReturn(List.of(requestDto));

        String result = mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getRequestsByUser(anyLong());
        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);
    }

    @SneakyThrows
    @Test
    void getYourRequests_whenNotUserId_thenReturnedClientError() {
        mockMvc.perform(get("/requests"))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).getRequestsByUser(anyLong());
    }

    @SneakyThrows
    @Test
    void addRequest_whenRequestCorrect_thenReturnedOk() {
        requestDto.setDescription("test");
        when(requestService.addRequest(anyLong(), any())).thenReturn(requestDto);

        String result = mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).addRequest(anyLong(), any());
        assertEquals(objectMapper.writeValueAsString(requestDto), result);
    }

    @SneakyThrows
    @Test
    void addRequest_whenDescriptionNotValid_thenReturnedClientError() {
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void addRequest_whenNotUserId_thenReturnedClientError() {
        requestDto.setDescription("test");
        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).addRequest(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenRequestCorrect_thenReturnedOk() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(requestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenReturn(List.of(requestDto));

        String result = mockMvc.perform(get("/requests/all")
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getAllRequests(anyLong(), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);
    }

    @SneakyThrows
    @Test
    void getAllRequests_whenNotUserId_thenReturnedClientError() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");

        mockMvc.perform(get("/requests/all")
                        .params(requestParams))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).getAllRequests(anyLong(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    void getRequestById_whenRequestCorrect_thenReturnOk() {
        when(requestService.getRequestById(anyLong(), anyLong())).thenReturn(requestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(requestService).getRequestById(anyLong(), anyLong());
        assertEquals(objectMapper.writeValueAsString(requestDto), result);
    }

    @SneakyThrows
    @Test
    void getRequestById_whenNotUserId_thenReturnClientError() {
        mockMvc.perform(get("/requests/{requestId}", 1))
                .andExpect(status().is4xxClientError());

        verify(requestService, never()).getRequestById(anyLong(), anyLong());
    }
}