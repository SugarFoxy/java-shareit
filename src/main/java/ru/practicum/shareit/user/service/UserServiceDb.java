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
        return UserMapper.toUserDto(user.orElseThrow(() -> new MissingObjectException("Невозможно найти. Пользователь отсутствует!")));
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Получен запрос на создание пользователя");
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new DuplicateException("Пользователь с таким email  уже существует");
        }
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
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
            if (userRepository.existsByEmail(userDto.getEmail())) {
                throw new DuplicateException("Пользователь с таким email  уже существует");
            }
            user.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long id) {
        log.info("Получен запрос на удаление пользователя");
        userRepository.deleteById(id);
    }
}
