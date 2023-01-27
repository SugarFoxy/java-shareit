package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
 class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User user = userRepository.save(User.builder()
                .id(1L)
                .name("Имя")
                .email("email@email.com")
                .build());
        entityManager.persist(user);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    void existsByEmailTest() {
        assertTrue(userRepository.existsByEmail("email@email.com"));
        assertFalse(userRepository.existsByEmail("netuTakogo@mail.ru"));
    }
}