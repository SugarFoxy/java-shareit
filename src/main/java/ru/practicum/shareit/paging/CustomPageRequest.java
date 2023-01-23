package ru.practicum.shareit.paging;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.InvalidRequestException;

public class CustomPageRequest extends PageRequest {
    private static final int MAX_PAGE_SIZE = Integer.MAX_VALUE;

    private final int offset;
    private final int size;
    private final Sort sort;

    private CustomPageRequest(int offset, int size, Sort sort) {
        super(0, size, sort);
        this.offset = offset;
        this.size = size;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public PageRequest next() {
        return new CustomPageRequest(offset + size, size, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new CustomPageRequest(offset, size, sort);
    }

    @Override
    public PageRequest first() {
        return new CustomPageRequest(offset, size, sort);
    }

    @Override
    public PageRequest withPage(int pageNumber) {
        return new CustomPageRequest(offset + size * pageNumber, size, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }

    public static Pageable create(Integer offset, Integer size) {
        return create(offset, size, Sort.unsorted());
    }

    public static Pageable create(Integer offset, Integer size, Sort sort) {
        if (offset == null && size == null) {
            return unpaged(sort);
        }
        validatePaging(offset, size);
        return new CustomPageRequest(offset, size, sort);
    }

    public static Pageable unpaged(Sort sort) {
        return new CustomPageRequest(0, MAX_PAGE_SIZE, sort);
    }

    private static void validatePaging(Integer from, Integer size) {
        if (from == null || size == null) throw new InvalidRequestException("Некоректные данные пагинации");
        if (size <= 0) throw new InvalidRequestException("размер страницы не может быть меньше 1");
        if (from < 0) throw new InvalidRequestException("элемент не может быть меньше 0");
    }
}

