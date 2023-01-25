package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.paging.CustomPageRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static ru.practicum.shareit.booking.model.BookingStatus.REJECTED;
import static ru.practicum.shareit.booking.model.BookingStatus.WAITING;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User firstUser;
    private User secondUser;
    private Item firstItem;
    private Item secondItem;
    private Booking firstBooking;
    private Booking secondBooking;
    private Booking thirdBooking;
    private Pageable pageable;

    @BeforeEach
    public void beforeEach() {
        firstUser = userRepository.save(new User(1L, "Имя первого", "first@email.com"));
        entityManager.persist(firstUser);
        secondUser = userRepository.save(new User(2L, "Имя второго", "second@email.com"));
        entityManager.persist(secondUser);
        firstItem = itemRepository.save(new Item(
                        1L,
                        "Название первого",
                        "Описание первого",
                        true,
                        secondUser,
                        null
                )
        );
        entityManager.persist(firstItem);
        secondItem = itemRepository.save(new Item(
                        2L,
                        "Название второго",
                        "Описание второго",
                        true,
                        firstUser,
                        null
                )
        );
        entityManager.persist(secondItem);
        firstBooking = bookingRepository.save(new Booking(
                        1L,
                        LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2022, 1, 3, 1, 1, 1),
                        firstItem,
                        firstUser,
                        REJECTED
                )
        );
        entityManager.persist(firstBooking);
        secondBooking = bookingRepository.save(new Booking(
                        2L,
                        LocalDateTime.of(2024, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        secondItem,
                        firstUser,
                        WAITING
                )
        );
        entityManager.persist(secondBooking);
        thirdBooking = bookingRepository.save(new Booking(
                        3L,
                        LocalDateTime.of(2022, 1, 1, 1, 1, 1),
                        LocalDateTime.of(2024, 1, 3, 1, 1, 1),
                        secondItem,
                        firstUser,
                        WAITING
                )
        );
        entityManager.persist(thirdBooking);
        entityManager.getEntityManager().getTransaction().commit();
        pageable = CustomPageRequest.create(0, 10);
    }

    @AfterEach
    public void afterEach() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }

    @Test
    void findByBooker() {
        List<Booking> result = bookingRepository.findByBooker(firstUser, pageable);
        assertThat(result, hasItems(firstBooking, secondBooking,thirdBooking));
    }

    @Test
    void findByBookerAndEndIsBefore() {
        List<Booking> result = bookingRepository.findByBookerAndEndIsBefore(firstUser, LocalDateTime.now(), pageable);
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByBookerAndStartIsAfter() {
        List<Booking> result = bookingRepository.findByBookerAndStartIsAfter(firstUser, LocalDateTime.now(), pageable);
        assertThat(result, hasItems(secondBooking));
        assertThat(result, not(hasItems(firstBooking,thirdBooking)));
    }

    @Test
    void findCurrentByBooker() {
        List<Booking> result = bookingRepository.findCurrentByBooker(firstUser, pageable);
        assertThat(result, hasItems(thirdBooking));
        assertThat(result, not(hasItems(secondBooking,firstBooking)));
    }

    @Test
    void findByBookerAndStatus() {
        List<Booking> result = bookingRepository.findByBookerAndStatus(firstUser, WAITING, pageable);
        assertThat(result, hasItems(thirdBooking, secondBooking));
        assertThat(result, not(hasItems(firstBooking)));
    }

    @Test
    void findByBookerAndItem() {
        List<Booking> result = bookingRepository.findByBookerAndItem(firstUser,firstItem);
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByOwner() {
        List<Booking> result = bookingRepository.findByOwner(secondUser,pageable);
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByOwnerAndEndIsBefore() {
        List<Booking> result = bookingRepository.findByOwnerAndEndIsBefore(secondUser,LocalDateTime.now(), pageable);
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByOwnerAndStartIsAfter() {
        List<Booking> result = bookingRepository.findByOwnerAndStartIsAfter(firstUser,LocalDateTime.now(), pageable);
        assertThat(result, hasItems(secondBooking));
        assertThat(result, not(hasItems(firstBooking,thirdBooking)));
    }

    @Test
    void findCurrentByOwner() {
        List<Booking> result = bookingRepository.findCurrentByOwner(firstUser, pageable);
        assertThat(result, hasItems(thirdBooking));
        assertThat(result, not(hasItems(secondBooking,firstBooking)));
    }

    @Test
    void findByOwnerAndStatus() {
        List<Booking> result = bookingRepository.findByOwnerAndStatus(secondUser, REJECTED ,pageable);
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByItemAndEndIsBefore() {
        List<Booking> result = bookingRepository.findByItemAndEndIsBefore(firstItem, LocalDateTime.now());
        assertThat(result, hasItems(firstBooking));
        assertThat(result, not(hasItems(secondBooking,thirdBooking)));
    }

    @Test
    void findByItemAndStartIsAfter() {
        List<Booking> result = bookingRepository.findByItemAndStartIsAfter(secondItem, LocalDateTime.now());
        assertThat(result, hasItems(secondBooking));
        assertThat(result, not(hasItems(firstBooking,thirdBooking)));
    }
}