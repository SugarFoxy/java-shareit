package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.model.Booking;
import ru.practicum.shareit.booking.dto.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBooker(User booker);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime end);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime start);

    @Query("select b from Booking b where b.booker = :booker and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByBooker(User booker);

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status);

    List<Booking> findByItem(Item item);

    @Query("select b from Booking b where b.item = :item and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByItem(Item item);

    List<Booking> findByItemAndEndIsBefore(Item item, LocalDateTime end);

    List<Booking> findByItemAndStartIsAfter(Item item, LocalDateTime start);

    List<Booking> findByItemAndStatus(Item item, BookingStatus status);

    List<Booking> findByBookerAndItem(User booker, Item item);
}
