package org.galaxy.server.repository;

import org.galaxy.server.model.Reservation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    public void testSaveAndFindReservation() {
        Reservation reservation = Reservation.builder()
                .restaurantId(1L)
                .reservationName("John Doe")
                .guestCount(4)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(2))
                .build();

        Reservation savedReservation = reservationRepository.save(reservation);

        assertThat(savedReservation.getId()).isNotNull();
        
        Optional<Reservation> foundReservation = reservationRepository.findById(savedReservation.getId());
        assertThat(foundReservation).isPresent();
        assertThat(foundReservation.get().getReservationName()).isEqualTo("John Doe");
    }
}
