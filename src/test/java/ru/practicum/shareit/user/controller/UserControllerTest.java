package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.User;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private User userCorrect;
    private User userDuplicateEmail;
    private User userNoEmail;
    private User userInvalidEmail;
    private User userUpdateName;
    private User userUpdateEmail;
    private User userUpdateAll;
    private User userUpdateInvalidEmail;

    @BeforeEach
    void beforeEach() {
        userCorrect = new User(1, "correct", "correct@mail.ru");
        userDuplicateEmail = new User(1, "duplicate", "correct@mail.ru");
        userNoEmail = new User(2, "no email", "");
        userInvalidEmail = new User(3, "invalid email", "invalid.email");
        userUpdateName = new User(4, "update name", null);
        userUpdateEmail = new User(5, null, "update@mail.ru");
        userUpdateAll = new User(6, "updateAll", "updateAll@mail.ru");
        userUpdateInvalidEmail = new User(7, null, "invalid.email");
    }

    @Test
    void postTest() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDuplicateEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userNoEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userInvalidEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void patchTest() throws Exception {
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userCorrect))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString((userUpdateName)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString((userUpdateEmail)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString((userUpdateAll)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString((userUpdateInvalidEmail)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}