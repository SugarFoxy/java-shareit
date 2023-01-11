package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceDb implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return UserMapper.toUserDto(user.orElseThrow(() -> new MissingObjectException("Невозможно и найти. Пользователь отсутствует!")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new DuplicateException(e.getMessage());
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        userDto.setId(id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new MissingObjectException("Невозможно изменить. Пользователь отсутствует!"));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        try {
            return UserMapper.toUserDto(userRepository.save(user));
        } catch (Exception e) {
        throw new DuplicateException(e.getMessage());
    }

    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
