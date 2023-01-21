package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    @Qualifier("bookingServiceDb")
    BookingService bookingService;
    BookingOutputDto bookingOut;
    BookingInputDto bookingInput;

    @BeforeEach
    void init() {
        bookingInput = new BookingInputDto();
        bookingOut = new BookingOutputDto();
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingCorrect_thenReturnedOk() {
        bookingInput = BookingInputDto.builder().itemId(1L).build();
        when(bookingService.createBooking(bookingInput, 1L)).thenReturn(bookingOut);

        String result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).createBooking(any(), anyLong());
        assertEquals(objectMapper.writeValueAsString(bookingOut), result);
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingNotValid_thenReturnedClientError() {
        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void createBooking_whenBookingNotUserId_thenReturnedClientError() {
        bookingInput = BookingInputDto.builder().itemId(1L).build();

        mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(bookingInput))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).createBooking(any(), anyLong());
    }

    @SneakyThrows
    @Test
    void approve_whenRequestCorrect_thenReturnedOk() {
        when(bookingService.updateApprove(1L, true, 1L)).thenReturn(bookingOut);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved","true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService, times(1)).updateApprove(anyLong(),anyBoolean(), anyLong());
        assertEquals(objectMapper.writeValueAsString(bookingOut), result);
    }

    @SneakyThrows
    @Test
    void approve_whenNotApprove_thenReturnedClientError() {
        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).updateApprove(anyLong(),anyBoolean(), anyLong());
    }

    @SneakyThrows
    @Test
    void approve_whenNotUserID_thenReturnedClientError() {
      mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved","true"))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).updateApprove(anyLong(),anyBoolean(), anyLong());

    }

    @SneakyThrows
    @Test
    void getBookingInfo_whenRequestCorrect_thenReturnedOk() {
        when(bookingService.getBookingInfo(anyLong(), anyLong())).thenReturn(bookingOut);

        String result = mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getBookingInfo(anyLong(),anyLong());
        assertEquals(objectMapper.writeValueAsString(bookingOut), result);
    }

    @SneakyThrows
    @Test
    void getBookingInfo_whenNotUserID_thenReturnedClientError() {
        mockMvc.perform(get("/bookings/{bookingId}", 1))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).getBookingInfo(anyLong(),anyLong());
    }

    @SneakyThrows
    @Test
    void getAllBookings_whenRequestCorrect_thenReturnedOk() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("state", "CURRENT");
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(bookingService.getAllBookings(anyLong(),any(),anyInt(),anyInt())).thenReturn(List.of(bookingOut));

        String result = mockMvc.perform(get("/bookings", 1)
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookings(anyLong(),any(),anyInt(),anyInt());
        assertEquals(objectMapper.writeValueAsString(List.of(bookingOut)), result);
    }

    @SneakyThrows
    @Test
    void getAllBookings_whenRequestCorrectNotState_thenReturnedOk() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(bookingService.getAllBookings(anyLong(),any(),anyInt(),anyInt())).thenReturn(List.of(bookingOut));

        String result = mockMvc.perform(get("/bookings", 1)
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookings(anyLong(),any(),anyInt(),anyInt());
        assertEquals(objectMapper.writeValueAsString(List.of(bookingOut)), result);
    }

    @SneakyThrows
    @Test
    void getAllBookings_whenNotUserId_thenReturnedClientError() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("state", "CURRENT");
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        mockMvc.perform(get("/bookings", 1)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).getAllBookings(anyLong(),any(),anyInt(),anyInt());
    }

    @SneakyThrows
    @Test
    void getAllBookingsForOwner_whenRequestCorrect_thenReturnedOk() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("state", "CURRENT");
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(bookingService.getAllBookingsForOwner(anyLong(),any(),anyInt(),anyInt())).thenReturn(List.of(bookingOut));

        String result = mockMvc.perform(get("/bookings/owner", 1)
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsForOwner(anyLong(),any(),anyInt(),anyInt());
        assertEquals(objectMapper.writeValueAsString(List.of(bookingOut)), result);
    }

    @SneakyThrows
    @Test
    void getAllBookingsForOwner_whenRequestCorrectNotState_thenReturnedOk() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        when(bookingService.getAllBookingsForOwner(anyLong(),any(),anyInt(),anyInt())).thenReturn(List.of(bookingOut));

        String result = mockMvc.perform(get("/bookings/owner", 1)
                        .params(requestParams)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(bookingService).getAllBookingsForOwner(anyLong(),any(),anyInt(),anyInt());
        assertEquals(objectMapper.writeValueAsString(List.of(bookingOut)), result);
    }

    @SneakyThrows
    @Test
    void getAllBookingsForOwner_whenNotUserId_thenReturnedClientError() {
        LinkedMultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
        requestParams.add("state", "CURRENT");
        requestParams.add("from", "0");
        requestParams.add("size", "10");
        mockMvc.perform(get("/bookings/owner", 1)
                        .params(requestParams))
                .andExpect(status().is4xxClientError());

        verify(bookingService, never()).getAllBookingsForOwner(anyLong(),any(),anyInt(),anyInt());
    }
}