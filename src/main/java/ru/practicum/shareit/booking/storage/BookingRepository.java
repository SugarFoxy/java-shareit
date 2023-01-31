package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBooker(User booker, Pageable pageable);

    List<Booking> findByBookerAndEndIsBefore(User booker, LocalDateTime end, Pageable pageable);

    List<Booking> findByBookerAndStartIsAfter(User booker, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b where b.booker = :booker and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByBooker(User booker, Pageable pageable);

    List<Booking> findByBookerAndStatus(User booker, BookingStatus status, Pageable pageable);

    List<Booking> findByBookerAndItem(User booker, Item item);

    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner")
    List<Booking> findByOwner(User owner, Pageable pageable);

    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.end <= :end")
    List<Booking> findByOwnerAndEndIsBefore(User owner, LocalDateTime end, Pageable pageable);

    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.start >= :start")
    List<Booking> findByOwnerAndStartIsAfter(User owner, LocalDateTime start, Pageable pageable);

    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.start <= current_timestamp and b.end >= current_timestamp")
    List<Booking> findCurrentByOwner(User owner, Pageable pageable);

    @Query("select b from Booking b, Item i where b.item = i and i.owner = :owner and b.status = :status")
    List<Booking> findByOwnerAndStatus(User owner, BookingStatus status, Pageable pageable);

    List<Booking> findByItemAndEndIsBefore(Item item, LocalDateTime end);

    List<Booking> findByItemAndStartIsAfter(Item item, LocalDateTime start);

}
