package org.galaxy.server.service;

import org.antlr.v4.runtime.misc.NotNull;
import org.galaxy.server.model.Reservation;
import org.galaxy.server.repository.ReservationRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<Reservation> getReservationsByRestaurantId(Long restaurantId){
        return reservationRepository.findByRestaurantId(restaurantId);
    }

    public List<Reservation> getReservationsByRestaurantIdAndTimeBetween(Long restaurantId, LocalDateTime startTime, LocalDateTime startTime2){
        return reservationRepository.findByRestaurantIdAndStartTimeBetween(restaurantId, startTime, startTime2);
    }

    public Reservation createReservation(Reservation reservation) {
        Reservation temp = reservation;
        if(reservation.getRestaurantId() == null || reservation.getReservationName() == null || reservation.getGuestCount() == null || reservation.getStartTime() == null || reservation.getEndTime() == null){
            throw new IllegalArgumentException("Reservation must have all required fields");
        }
        return reservationRepository.save(reservation);
    }
}
