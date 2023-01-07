package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService {
    private final UserStorage storage;

    @Autowired
    UserServiceImp(@Qualifier("userStorageImp") UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public List<UserDto> getUsers() {
        return storage.getUsers().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        return UserMapper.toUserDto(storage.getUserById(id));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(storage.addUser(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        return UserMapper.toUserDto(storage.updateUser(UserMapper.toUser(userDto)));
    }

    @Override
    public void deleteUser(Long id) {
        storage.deleteUser(id);
    }
}
