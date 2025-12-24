package org.galaxy.server.service;

import org.galaxy.server.model.Reservation;
import org.galaxy.server.repository.ReservationRepository;
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
}
