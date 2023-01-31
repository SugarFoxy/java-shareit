package ru.practicum.shareit.user.client;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public class UserClient {
    private static final String BASE_URL = "http://localhost:9090/users/";
    private final WebClient webClient = WebClient.create(BASE_URL);
    ;

    public UserDto getUserById(final Long id) {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}/")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }

    public UserDto createUser(UserDto user) {
        return webClient
                .post()
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new DuplicateException("Пользовыатель с такой почтой уже существует")))
                .bodyToMono(UserDto.class)
                .block();
    }

    public List<UserDto> getAllUsers() {
        return webClient
                .get()
                .uri(BASE_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<UserDto>>() {
                })
                .block();
    }

    public UserDto updateUser(Long id, UserDto user) {
        return webClient
                .patch()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}/")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError,
                        error -> Mono.error(new DuplicateException("Пользовыатель с такой почтой уже существует")))
                .bodyToMono(UserDto.class)
                .block();
    }

    public void deleteUser(final Long id) {
        webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/{userId}/")
                        .build(id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(Void.class)
                .subscribe();
    }
}
