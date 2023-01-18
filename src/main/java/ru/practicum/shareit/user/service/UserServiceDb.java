package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserServiceDb implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getUsers() {
        log.info("Получен запрос на получение списка пользователей");
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(Long id) {
        log.info("Получен запрос на получение пользователя");
        Optional<User> user = userRepository.findById(id);
        return UserMapper.toUserDto(user.orElseThrow(() -> new MissingObjectException("Невозможно и найти. Пользователь отсутствует!")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        try {
            return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
        } catch (Exception e) {
            throw new DuplicateException("Пользователь с таким email уже существует");
        }
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long id) {
        log.info("Получен запрос на изменение пользователя");
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
        log.info("Получен запрос на удаление пользователя");
        userRepository.deleteById(id);
    }
}
