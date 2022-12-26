package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserStorageImp implements UserStorage {
    Map<Integer, User> users = new HashMap<>();
    int id = 1;

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос на вывод всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        checkAvailability("найти", id);
        log.info("Получен запрос на вывод пользователя по id");
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        isExist(user.getEmail());
        user.setId(getId());
        users.put(user.getId(), user);
        log.info("Получен запрос на добавление пользователя");
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkAvailability("изменить", user.getId());
        User updateUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank())
            updateUser.setName(user.getName());
        if (user.getEmail() != null) {
            isExist(user.getEmail());
            updateUser.setEmail(user.getEmail());
        }
        log.info("Получен запрос на изменение пользователя");
        return updateUser;
    }

    @Override
    public void deleteUser(Integer id) {
        checkAvailability("удалить", id);
        users.remove(id);
        log.info("Получен запрос на удаление пользователя");
    }

    private void checkAvailability(String operation, int id) {
        String massage = String.format("Невозможно %s. Пользователь отсутствует!", operation);
        if (!users.containsKey(id))
            throw new MissingObjectException(massage);
    }

    private void isExist(String email) {
        boolean doesItExist = false;
        for (User user : users.values()) {
            if (user.getEmail().equals(email)) {
                doesItExist = true;
                break;
            }
        }
        if (doesItExist) {
            throw new ValidationException("Пользователь с такой почто уже есть");
        }
    }

    private int getId() {
        return id++;
    }
}
