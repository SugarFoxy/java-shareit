package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    @Qualifier("userServiceDb")
    UserService userService;
    private User userCorrect;
    private User userDuplicateEmail;
    private User userNoEmail;
    private User userInvalidEmail;
    private User userUpdateInvalidEmail;
    private User user;

    @BeforeEach
    void beforeEach() {
        user = new User(3L, "correct", "user@mail.ru");
        userCorrect = new User(2L, "correct", "correct@mail.ru");
        userDuplicateEmail = new User(null, "duplicate", "correct@mail.ru");
        userNoEmail = new User(2L, "no email", "");
        userInvalidEmail = new User(3L, "invalid email", "invalid.email");
        userUpdateInvalidEmail = new User(7L, null, "invalid.email");
    }

    @SneakyThrows
    @Test
    void createUser_whenUserCorrect_thenReturnedOk() {
        UserDto userDto = UserMapper.toUserDto(userCorrect);
        when(userService.createUser(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailDuplication_thenThrow() {
        UserDto userDtoDuplication = UserMapper.toUserDto(userDuplicateEmail);
        when(userService.createUser(userDtoDuplication))
                .thenThrow(new DuplicateException("Пользователь с таким email  уже существует"));

        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoDuplication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals("{\"error\":\"Пользователь с таким email  уже существует\"}", result);
    }

    @SneakyThrows
    @Test
    void createUser_whenNoEmail_thenReturnedBadRequest() {
        UserDto userDtoNoEmail = UserMapper.toUserDto(userNoEmail);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoNoEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void createUser_whenEmailInvalid_thenReturnedBadRequest() {
        UserDto userDtoInvalidEmail = UserMapper.toUserDto(userInvalidEmail);

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoInvalidEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).createUser(any());
    }

    @SneakyThrows
    @Test
    void updateUser_whenUserCorrect_thenReturnedOk() {
        UserDto userDtoToUpdate = UserMapper.toUserDto(user);
        when(userService.updateUser(userDtoToUpdate, 1L)).thenReturn(userDtoToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", 1L)
                        .content(objectMapper.writeValueAsString(userDtoToUpdate))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToUpdate), result);
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailDuplication_thenThrow() {
        UserDto userDtoDuplication = UserMapper.toUserDto(userDuplicateEmail);
        when(userService.updateUser(userDtoDuplication, 1L))
                .thenThrow(new DuplicateException("Пользователь с таким email  уже существует"));

        String result = mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDtoDuplication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        assertEquals("{\"error\":\"Пользователь с таким email  уже существует\"}", result);
        verify(userService).updateUser(userDtoDuplication, 1L);
    }

    @SneakyThrows
    @Test
    void updateUser_whenEmailInvalid_thenReturnedBadRequest() {
        UserDto userDtoInvalidEmail = UserMapper.toUserDto(userUpdateInvalidEmail);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .content(objectMapper.writeValueAsString(userDtoInvalidEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(userService, never()).updateUser(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserPresent_thenReturnedOk() {
        long userId = 1L;

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getUserById_whenUserNotPresent_thenReturnedOk() {
        long userId = 1L;
        when(userService.getUserById(userId)).thenThrow(new MissingObjectException("Невозможно найти. Пользователь отсутствует!"));

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().is4xxClientError());

        verify(userService).getUserById(userId);
    }

    @SneakyThrows
    @Test
    void getUsersTest() {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getUsers();
    }

    @Test
    public void deleteTestSuccess() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());

        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().is2xxSuccessful());
    }


}