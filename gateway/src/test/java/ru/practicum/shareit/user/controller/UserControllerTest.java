package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserClient client;
    private UserDto userDto;

    @BeforeEach
    void init() {
        userDto = new UserDto();
    }

    @SneakyThrows
    @Test
    void createUser_whenUserCorrect_thenReturnedOk() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email("testemail@mail.ru")
                .build();
        when(client.createUser(any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailNotFound_thenReturnedClientError() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email(null)
                .build();

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmptyEmail_thenReturnedClientError() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email("")
                .build();

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailInvalid_thenReturnedBadRequest() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email("skjfgh;skj")
                .build();

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserCorrect_thenReturnedOk() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email("")
                .build();
        when(client.updateUser(anyLong(), any())).thenReturn(ResponseEntity.ok(userDto));

        mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailInvalid_thenReturnedBadRequest() {
        userDto = UserDto.builder()
                .id(1L)
                .name("testname")
                .email("skjfgh;skj")
                .build();

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }
}