package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserStorageImp implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, User> usersEmail = new HashMap<>();
    private int id = 1;

    @Override
    public List<User> getUsers() {
        log.info("Получен запрос на вывод всех пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(Integer id) {
        checkUserAvailability("найти", id);
        log.info("Получен запрос на вывод пользователя по id");
        return users.get(id);
    }

    @Override
    public User addUser(User user) {
        isExist(user.getEmail());
        user.setId(getId());
        users.put(user.getId(), user);
        usersEmail.put(user.getEmail(), user);
        log.info("Получен запрос на добавление пользователя");
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserAvailability("изменить", user.getId());
        User updateUser = users.get(user.getId());
        String email = updateUser.getEmail();
        User updateEmail = usersEmail.get(email);
        if (user.getName() != null && !user.getName().isBlank()) {
            updateUser.setName(user.getName());
            updateEmail.setName(user.getName());
        }
        if (user.getEmail() != null) {
            isExist(user.getEmail());
            updateUser.setEmail(user.getEmail());
            usersEmail.remove(email);
            usersEmail.put(updateUser.getEmail(), updateUser);
        }
        log.info("Получен запрос на изменение пользователя");
        return updateUser;
    }

    @Override
    public void deleteUser(Integer id) {
        checkUserAvailability("удалить", id);
        users.remove(id);
        log.info("Получен запрос на удаление пользователя");
    }

    @Override
    public void checkUserAvailability(String operation, int id) {
        String massage = String.format("Невозможно %s. Пользователь отсутствует!", operation);
        if (!users.containsKey(id)) {
            throw new MissingObjectException(massage);
        }
    }

    private void isExist(String email) {
        if (usersEmail.containsKey(email)) {
            throw new DuplicateException("Пользователь с такой почтой уже есть");
        }
    }

    private int getId() {
        return id++;
    }
}
