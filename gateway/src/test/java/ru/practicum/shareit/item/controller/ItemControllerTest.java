package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemClient client;
    private ItemDto itemDto;

    @BeforeEach
    void init() {
        itemDto = new ItemDto();
    }

    @SneakyThrows
    @Test
    void itemCreate_whenItemCorrect_thenReturnedOk() {
        itemDto = ItemDto.builder().name("name").description("desc").available(true).build();
        when(client.creatItem(anyLong(), any())).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void itemCreate_whenUserIdNotPresent_thenReturnedClientError() {
        itemDto = ItemDto.builder().name("name").description("desc").available(true).build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void itemCreat_whenNotName_thenRequestedClientError() {
        itemDto = ItemDto.builder().description("desc").available(true).build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void itemCreat_whenNotDescription_thenRequestedClientError() {
        itemDto = ItemDto.builder().name("name").available(true).build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void itemCreat_whenNotAvailable_thenRequestedClientError() {
        itemDto = ItemDto.builder().name("name").description("desc").build();

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
    }


    @SneakyThrows
    @Test
    void updateItem_whenItemCorrect_thenReturnedOk() {
        itemDto = ItemDto.builder().name("name").description("desc").available(true).build();
        when(client.updateItem(anyLong(), anyLong(), any())).thenReturn(ResponseEntity.ok(itemDto));

        mockMvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }


    @SneakyThrows
    @Test
    void updateItem_whenUserIdNotPresent_thenReturnedClientError() {
        itemDto = ItemDto.builder().name("name").description("desc").available(true).build();

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is4xxClientError());
    }
}