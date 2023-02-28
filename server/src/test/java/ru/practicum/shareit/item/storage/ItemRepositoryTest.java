package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
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
    private ItemRequest firstRequest;
    private Item firstItem;
    private Item secondItem;
    private Item thirdItem;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        testEntityManager.persist(firstUser);
        User secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        testEntityManager.persist(secondUser);
        firstRequest = itemRequestRepository.save(new ItemRequest(1L, "Реквест на первый и второй", firstUser, LocalDateTime.now()));
        testEntityManager.persist(firstRequest);
        ItemRequest secondRequest = itemRequestRepository.save(new ItemRequest(2L, "Реквест на третий", firstUser, LocalDateTime.now()));
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
        itemRequestRepository.deleteAll();
        itemRepository.deleteAll();
        userRepository.deleteAll();

    }

    @Test
    public void findAllByOwnerTest() {
        List<Item> items = itemRepository.findByOwner(firstUser, pageable);

        assertThat(items, hasItems(firstItem, secondItem));
        assertThat(items, not(hasItems(thirdItem)));
    }

    @Test
    public void findAllByRequestTest() {
        List<Item> items = itemRepository.findByRequest(firstRequest);

        assertThat(items, hasItems(firstItem, secondItem));
        assertThat(items, not(hasItems(thirdItem)));
    }

    @Test
    public void findByText() {
        List<Item> items = itemRepository.search("вТоРоГо", pageable);

        assertThat(items, hasItems(secondItem));
        assertThat(items, not(hasItems(firstItem, thirdItem)));
    }

    @Test
    public void getItemOwnerTest() {
        User result = itemRepository.getItemOwner(1L);
        assertEquals(firstUser, result);
    }
}
