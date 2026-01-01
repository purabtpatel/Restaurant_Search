package org.galaxy.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.galaxy.server.model.Reservation;
import org.galaxy.server.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest {

    private MockMvc mockMvc;
    private ReservationService reservationService;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        reservationService = Mockito.mock(ReservationService.class);
        ReservationController reservationController = new ReservationController(reservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    void testGetReservationsByRestaurantIdOnly() throws Exception {
        Reservation r1 = Reservation.builder().id(1L).restaurantId(1).reservationName("Guest 1").build();
        Reservation r2 = Reservation.builder().id(2L).restaurantId(1).reservationName("Guest 2").build();
        List<Reservation> reservations = Arrays.asList(r1, r2);

        when(reservationService.getReservationsByRestaurantId(1)).thenReturn(reservations);

        mockMvc.perform(get("/reservations/restaurant/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].reservationName").value("Guest 1"))
                .andExpect(jsonPath("$[1].reservationName").value("Guest 2"));
    }

    @Test
    void testGetReservationsWithTimeRange() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 12, 22, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 22, 12, 0);
        Reservation r1 = Reservation.builder().id(1L).restaurantId(1).reservationName("Guest 1").startTime(start).build();
        List<Reservation> reservations = List.of(r1);

        when(reservationService.getReservationsByRestaurantIdAndTimeBetween(anyInt(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(reservations);

        mockMvc.perform(get("/reservations/restaurant/1")
                        .param("start", "2025-12-22T10:00:00")
                        .param("end", "2025-12-22T12:00:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].reservationName").value("Guest 1"));
    }
    @Test
    void testPostReservations() throws Exception {
        LocalDateTime start = LocalDateTime.of(2025, 12, 26, 10, 0);
        LocalDateTime end = LocalDateTime.of(2025, 12, 26, 11, 0);
        Integer restaurantId = 3;
        Integer guestCount = 2;
        String reservationName = "John Doe";

        Reservation reservation = Reservation.builder().restaurantId(restaurantId).reservationName(reservationName).guestCount(guestCount).startTime(start).endTime(end).build();

        mockMvc.perform(post("/reservations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isOk());
    }

    @Test
    void testPostReservationsWithInvalidData() throws Exception {
        Reservation reservation = Reservation.builder().build();
        when(reservationService.createReservation(any(Reservation.class)))
                .thenThrow(new IllegalArgumentException("Reservation must have all required fields"));

        mockMvc.perform(post("/reservations")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(reservation)))
                .andExpect(status().isBadRequest());
    }
}
