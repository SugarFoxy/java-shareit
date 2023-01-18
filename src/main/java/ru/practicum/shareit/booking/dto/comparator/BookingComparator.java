package ru.practicum.shareit.booking.dto.comparator;

import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.time.LocalDateTime;
import java.util.Comparator;

public class BookingComparator implements Comparator<BookingOutputDto> {

    @Override
    public int compare(BookingOutputDto o1, BookingOutputDto o2) {
        LocalDateTime start1 = o1.getStart();
        LocalDateTime start2 = o2.getStart();

        if (start1.isEqual(start2)) {
            return 0;
        } else if (start1.isBefore(start2)) {
            return -1;
        }
        return 1;
    }
}
