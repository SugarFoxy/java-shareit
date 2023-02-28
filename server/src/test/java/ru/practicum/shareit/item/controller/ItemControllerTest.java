package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    @Qualifier("itemServiceDb")
    private ItemService itemService;
    @MockBean
    @Qualifier("itemServiceDb")
    private CommentService commentService;
    private User user;
    private User incorrectOwner;
    private Item itemCorrect;

    @BeforeEach
    void beforeEach() {
        user = new User(1L, "correct", "forItem@mail.ru");
        incorrectOwner = new User(4L, "incorrect", "incorrect@mail.ru");
        itemCorrect = new Item(1L, "correct", "correct desc", true, user, null);
    }

    @SneakyThrows
    @Test
    void itemCreate_whenItemCorrect_thenReturnedOk() {
        ItemDto itemDto = ItemMapper
                .toItemDto(itemCorrect);
        when(itemService.creatItem(1L, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void itemCreate_whenUserIdNotPresent_thenReturnedClientError() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);

        String result = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).creatItem(anyLong(), any());
        assertEquals("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter type Long is not present\"}", result);
    }

    @SneakyThrows
    @Test
    void itemCreat_whenIncorrectOwner_thenRequestedClientError() {
        ItemDto itemDto = ItemMapper
                .toItemDto(new Item(1L,
                        "correct",
                        "correct desc",
                        true,
                        incorrectOwner,
                        null));
        when(itemService.creatItem(2L, itemDto)).thenThrow(new MissingObjectException("Невозможно найти. Пользователь отсутствует!"));

        String result = mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 2))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals("{\"error\":\"Невозможно найти. Пользователь отсутствует!\"}", result);
        verify(itemService, times(1)).creatItem(anyLong(), any());
    }

    @SneakyThrows
    @Test
    void updateItem_whenItemCorrect_thenReturnedOk() {
        ItemDto itemDto = ItemMapper
                .toItemDto(new Item(1L, "update", "all update", false, user, null));
        when(itemService.updateItem(1L, itemDto, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON).header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateItem_whenIncorrectOwner_thenRequestedClientError() {
        ItemDto itemDto = ItemMapper.toItemDto(
                new Item(1L, "correct", "correct desc", true, incorrectOwner, null));
        when(itemService.updateItem(2L, itemDto, 1L)).thenThrow(new MissingObjectException("Невозможно найти. Пользователь отсутствует!"));

        String result = mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals("{\"error\":\"Невозможно найти. Пользователь отсутствует!\"}", result);

        verify(itemService, times(1)).updateItem(anyLong(), any(), anyLong());
    }

    @SneakyThrows
    @Test
    void updateItem_whenUserIdNotPresent_thenReturnedClientError() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);

        String result = mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).updateItem(anyLong(), any(), anyLong());
        assertEquals("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter type Long is not present\"}", result);
    }

    @SneakyThrows
    @Test
    void getItemsByUser_whenCorrectOwner_thenReturnedOk() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);
        List<ItemDto> items = List.of(itemDto);
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(itemService.getItemsByUser(1L, 0, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).getItemsByUser(anyLong(), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void getItemsByUser_whenNotOwner_thenReturnedClientError() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");

        String result = mockMvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .params(requestParams))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, never()).getItemsByUser(anyLong(), anyInt(), anyInt());
        assertEquals("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter type Long is not present\"}", result);
    }

    @SneakyThrows
    @Test
    void getItemsByText_whenSearch_thenReturnedOk() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);
        List<ItemDto> items = List.of(itemDto);
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("text", "test");
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(itemService.getItemByText("test", 0, 10)).thenReturn(items);

        String result = mockMvc.perform(get("/items/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService, times(1)).getItemByText(anyString(), anyInt(), anyInt());
        assertEquals(objectMapper.writeValueAsString(items), result);
    }

    @SneakyThrows
    @Test
    void getItemById_whenOwnerPresent_thenReturnedOk() {
        ItemDto itemDto = ItemMapper.toItemDto(itemCorrect);
        when(itemService.getItemById(1L, 1L)).thenReturn(itemDto);

        String result = mockMvc.perform(get("/items/{userId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).getItemById(anyLong(), anyLong());
        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void getItemById_whenOwnerNotPresent_thenReturnedClientError() {
        String result = mockMvc.perform(get("/items/{userId}", 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService, never()).getItemById(anyLong(), anyLong());
        assertEquals("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter type Long is not present\"}", result);
    }

    @SneakyThrows
    @Test
    void getItemById_whenItemIdNotFound_thenReturnedClientError() {
        when(itemService.getItemById(2L, 1L)).thenThrow(new MissingObjectException("Невозможно найти. вещь отсутствует!"));

        String result = mockMvc.perform(get("/items/{userId}", 2)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        verify(itemService).getItemById(anyLong(), anyLong());
        assertEquals("{\"error\":\"Невозможно найти. вещь отсутствует!\"}", result);
    }

    @SneakyThrows
    @Test
    void postComment_whenCorrectComment_thenReturnedOk() {
        CommentDto comment = CommentDto.builder().text("test").build();
        when(commentService.addComment(1L, 1L, comment)).thenReturn(comment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        verify(commentService).addComment(anyLong(), anyLong(), any());
        assertEquals(objectMapper.writeValueAsString(comment), result);
    }

    @SneakyThrows
    @Test
    void postComment_whenCorrectNotUserId_thenReturnedClientError() {
        CommentDto comment = CommentDto.builder().text("test").build();
        when(commentService.addComment(1L, 1L, comment)).thenReturn(comment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .content(objectMapper.writeValueAsString(comment))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        verify(commentService, never()).addComment(anyLong(), anyLong(), any());
        assertEquals("{\"error\":\"Required request header 'X-Sharer-User-Id' for method parameter type Long is not present\"}", result);
    }
}