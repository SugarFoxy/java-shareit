package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User firstUser;
    private User secondUser;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя", "email@email.com"));
        entityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя2", "email2@email.com"));
        entityManager.persist(secondUser);
        firstRequest = itemRequestRepository
                .save(new ItemRequest(1L, "Описание первого", firstUser, LocalDateTime.now()));
        entityManager.persist(firstRequest);
        secondRequest = itemRequestRepository
                .save(new ItemRequest(2L, "Описание второго", secondUser, LocalDateTime.now()));
        entityManager.persist(secondRequest);
        entityManager.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    void findByRequestor() {
        List<ItemRequest> result = itemRequestRepository.findByRequestor(firstUser);
        assertThat(result, hasItems(firstRequest));
        assertThat(result, not(hasItems(secondRequest)));
    }

    @Test
    void findByRequestorNot() {
        List<ItemRequest> result = itemRequestRepository
                .findByRequestorNot(secondUser, CustomPageRequest.create(0,10));
        assertThat(result, hasItems(firstRequest));
        assertThat(result, not(hasItems(secondRequest)));
    }
}