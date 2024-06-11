package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingClient client;
    private BookingInputDto bookingInput;

    @BeforeEach
    void init() {
        bookingInput = new BookingInputDto();
    }

    @SneakyThrows
    @Test
    void createBooking_whenInvalidRequest_thenStatus4xxOr5xx() {
// not item
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
// not user ID
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void approve_whenInvalidRequest_thenStatus4xxOr5xx() {
// not approved
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());
// not user ID
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", "true"))
                .andExpect(status().is4xxClientError());
    }

    @SneakyThrows
    @Test
    void createBooking_whenValidRequest_thenStatusOk() {
        bookingInput.setItemId(1L);
        when(client.createBooking(anyLong(),any())).thenReturn(ResponseEntity.ok("тело"));

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }

    @SneakyThrows
    @Test
    void approve_whenValidRequest_thenStatusOk() {
        when(client.updateApprove(anyLong(),anyBoolean(),anyLong())).thenReturn(ResponseEntity.ok("тело"));

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful());
    }
}