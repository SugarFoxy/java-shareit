package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    User user;
    Item itemCorrect;
    Item itemNullName;
    Item itemNullDescription;
    Item itemNullAvailable;
    Item itemIncorrectOwner;
    Item itemUpdate;
    Item itemUpdateName;
    Item itemUpdateDescription;
    Item itemUpdateAvailable;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "correct", "forItem@mail.ru");
        itemCorrect = new Item(1, "correct", "correct desc", true, 1);
        itemNullName = new Item(1, null, "null name", true, 1);
        itemNullDescription = new Item(1, "null desc", null, true, 1);
        itemNullAvailable = new Item(1, "null available", "null available", null, 1);
        itemIncorrectOwner = new Item(1, "correct", "correct desc", true, 4);
        itemUpdate = new Item(1, "update", "all update", false, 1);
        itemUpdateName = new Item(1, "update name", null, null, 1);
        itemUpdateDescription = new Item(1, null, "update desc", null, 1);
        itemUpdateAvailable = new Item(1, null, null, true, 1);
    }

    @Test
    void itemPostTest() throws Exception {
        mockMvc.perform(post("/users")
                .content(objectMapper.writeValueAsString(user))
                .contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemNullName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemNullDescription))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemNullAvailable))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemIncorrectOwner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void itemPatchTest() throws Exception {
        mockMvc.perform(post("/items")
                        .content(objectMapper.writeValueAsString(itemCorrect))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemIncorrectOwner))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemIncorrectOwner))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemUpdateName))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemNullAvailable))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .content(objectMapper.writeValueAsString(itemUpdateDescription))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }
}