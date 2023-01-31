package ru.practicum.shareit.item.comment.storage;

import org.junit.jupiter.api.AfterEach;
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
    private Comment comment;

    @BeforeEach
    void save() {
        User owner = userRepository.save(User.builder().id(1L).name("name").email("name@mail.ru").build());
        em.persist(owner);
        Item item = itemRepository.save(Item.builder()
                .id(1L)
                .name("Огнетушитель")
                .description("Пользуйтесь при написании тестов ДЛЯ ВСЕГО КОДА")
                .owner(owner)
                .build());
        em.persist(item);
        User commentator = userRepository.save(User.builder()
                .id(2L)
                .name("")
                .email("goryashchie@perdaki.com")
                .build());
        em.persist(commentator);
        comment = commentRepository.save(new Comment(2L, "text", item, commentator, LocalDateTime.now()));
        em.persist(comment);
        em.getEntityManager().getTransaction().commit();
    }

    @AfterEach
    void delete() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }

    @Test
    void findByItemId() {
        List<Comment> comments = commentRepository.findByItemId(1L);
        assertThat(comments, hasItems(comment));
    }
}