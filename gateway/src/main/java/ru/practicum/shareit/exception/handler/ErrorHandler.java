package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.exception.InvalidRequestException;
import ru.practicum.shareit.exception.MissingObjectException;
import ru.practicum.shareit.exception.OtherDataException;
import ru.practicum.shareit.user.controller.UserController;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice(assignableTypes = {UserController.class})
public class ErrorHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final MethodArgumentNotValidException e) {
        String[] allErrors = e.getAllErrors().toString().split(";");
        String massage = allErrors[allErrors.length - 1];
        Map<String, String> error = Map.of("error", massage);
        log.warn(massage);
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final MissingObjectException e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        log.warn(e.getMessage());
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handle(final DuplicateException e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        log.warn(e.getMessage());
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final InvalidRequestException e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        log.warn(e.getMessage());
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final MissingRequestHeaderException e) {
        Map<String, String> error = Map.of("error", Objects.requireNonNull(e.getMessage()));
        log.warn(e.getMessage());
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handle(final OtherDataException e) {
        Map<String, String> error = Map.of("error", e.getMessage());
        log.warn(e.getMessage());
        return error;
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handle(final IllegalArgumentException e) {
        Map<String, String> error = Map.of("error", "Unknown state: UNSUPPORTED_STATUS");
        log.warn(e.getMessage());
        return error;
    }
}
