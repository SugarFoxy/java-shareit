package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.user.controller.UserController;

import java.util.Map;
import java.util.Objects;
import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class ErrorHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final ValidationException e) {
        log.warn(e.getMessage());
        return Map.of("error", Objects.requireNonNull(e.getMessage()));
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final IllegalArgumentException e) {
        log.warn(e.getMessage());
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final MissingRequestHeaderException e) {
        log.warn(e.getMessage());
        return Map.of("error", Objects.requireNonNull(e.getMessage()));
    }
}
