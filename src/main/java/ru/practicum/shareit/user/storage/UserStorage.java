package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User getUserById(Integer id);

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(Integer id);

    void checkUserAvailability(String operation, int id);
}
