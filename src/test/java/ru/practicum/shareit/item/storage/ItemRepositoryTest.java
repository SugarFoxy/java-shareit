package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager testEntityManager;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private Pageable pageable;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;
    private ItemRequest firstRequest;
    private ItemRequest secondRequest;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        testEntityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        testEntityManager.persist(secondUser);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Реквест на первый и второй", firstUser, LocalDateTime.now()));
        testEntityManager.persist(firstRequest);
        secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Реквест на третий", firstUser, LocalDateTime.now()));
        testEntityManager.persist(secondRequest);
        firstItem = itemRepository.save(new Item(1L, "Название первого", "Описание первого", true, firstUser, firstRequest));
        testEntityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(2L, "Название второго", "Описание второго", true, firstUser, firstRequest));
        testEntityManager.persist(secondItem);
        thirdItem = itemRepository.save(new Item(3L, "Название третьего", "Описание третьего", true, secondUser, secondRequest));
        testEntityManager.persist(thirdItem);
        testEntityManager.getEntityManager().getTransaction().commit();
        pageable = CustomPageRequest.create(0, 10);
    }

    @AfterEach
    public void afterEach() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();
    }

    @Test
    public void findAllByOwnerTest() {
        List<Item> items = itemRepository.findByOwner(firstUser, pageable);

        assertEquals(2, items.size());
    }

    @Test
    public void findAllByRequestIdTest() {
        List<Item> items = itemRepository.findByRequest(firstRequest);

        assertEquals(2, items.size());
    }

    @Test
    public void findByText() {
        List<Item> items = itemRepository.search("вТоРоГо", pageable);

        assertEquals(1, items.size());
    }

    @Test
    public void getItemOwnerTest() {
        User result = itemRepository.getItemOwner(1L);
        assertEquals(firstUser, result);
    }
}
