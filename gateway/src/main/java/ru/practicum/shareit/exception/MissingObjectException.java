package ru.practicum.shareit.exception;

public class MissingObjectException extends RuntimeException {
    public MissingObjectException(String message) {
        super(message);
    }
}
