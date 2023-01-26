package ru.practicum.shareit.item.comment.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private final Item item = Item.builder()
            .id(1L)
            .name("Огнетушитель")
            .description("Пользуйтесь при написании тестов ДЛЯ ВСЕГО КОДА")
            .build();
    private final User commentator = User.builder()
            .id(1L)
            .name("")
            .email("goryashchie@perdaki.com")
            .build();
    Comment comment;

    @BeforeEach
    void save() {
        Item saveItem = itemRepository.save(item);
        em.persist(saveItem);
        User saveUser = userRepository.save(commentator);
        em.persist(saveUser);
        comment = commentRepository.save(new Comment(1L, "text", item, commentator, LocalDateTime.now()));
        em.persist(comment);
    }

    @Test
    public void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(1L);
        assertThat(comments, hasItems(comment));
    }
}