package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers();

    UserDto getUserById(Integer id);

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Integer id);

    void deleteUser(Integer id);
}
