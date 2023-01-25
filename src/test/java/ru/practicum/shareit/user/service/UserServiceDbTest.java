package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceDbTest {
    @Mock
    UserRepository userRepository;
    @InjectMocks
    UserServiceDb userServiceDb;

    @BeforeEach
    public void before() {
        when(userRepository.save(any())).thenAnswer(input -> input.getArguments()[0]);
    }

    @Test
    void getUsers_whenFindAll_thenReturnedUserList() {
        User user = new User();
        List<User> usersOutRep = List.of(user);

        when(userRepository.findAll()).thenReturn(usersOutRep);

        List<UserDto> usersDto = userServiceDb.getUsers();
        verify(userRepository).findAll();
        assertEquals(1, usersDto.size());
    }

    @Test
    void getUserById_whenUserPresent_thenUser() {
        Long userId = 0L;
        User user = new User();
        UserDto userDto = new UserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDtoActual = userServiceDb.getUserById(userId);

        verify(userRepository).findById(userId);
        assertEquals(userDto, userDtoActual);
    }

    @Test
    void getUserById_whenUserNotFound_thenTrowException() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(MissingObjectException.class,
                () -> userServiceDb.getUserById(userId));
    }

    @Test
    void createUser_whenCorrectUser_thenSave() {
        Long userId = 0L;
        User user = User.builder()
                .id(userId)
                .email("user@mail.ru")
                .name("user").build();
        when(userRepository.save(user)).thenReturn(user);

        UserDto userDto = userServiceDb.createUser(UserMapper.toUserDto(user));

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
        assertEquals(UserMapper.toUserDto(user), userDto);
    }

    @Test
    void createUser_whenDuplicationEmail_thenNotSave() {
        Long userId = 0L;
        User user = User.builder()
                .id(userId)
                .email("user@mail.ru")
                .name("user").build();
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userServiceDb.createUser(UserMapper.toUserDto(user)));

        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
    }

    @Test
    void updateUser_whenRequestCorrect_thenReturnedUpdateUser() {
        User user = User.builder()
                .id(1L)
                .name("user_old")
                .email("user_old@yandex.ru").build();
        when(userRepository.findById(eq(1L))).thenReturn(Optional.of(user));
        UserDto userDto = UserDto.builder()
                .id(null).name("user_new").email("user_new@yandex.ru")
                .build();
        userServiceDb.updateUser(userDto, 1L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User capturedUser = captor.getValue();
        assertEquals("user_new", capturedUser.getName());
        assertEquals("user_new@yandex.ru", capturedUser.getEmail());
    }

    @Test
    void updateUser_whenDuplicateEmail_thenThrowException() {
        User user = User.builder().id(1L).name("user").email("user@mail.ru").build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(DuplicateException.class,
                () -> userServiceDb.updateUser(UserMapper.toUserDto(user), 1L));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_whenUserNorFound_thenThrownException() {
        assertThrows(
                MissingObjectException.class,
                () -> {
                    when(userRepository.findById(eq(123L))).thenReturn(Optional.empty());
                    userServiceDb.getUserById(123L);
                }
        );
    }

    @Test
    public void deleteUserById_deletes() {
        userServiceDb.deleteUser(0L);
        verify(userRepository).deleteById(0L);
    }
}