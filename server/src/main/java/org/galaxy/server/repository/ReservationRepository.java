package org.galaxy.server.repository;

import org.galaxy.server.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for {@link Reservation} entities.
 */
@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByRestaurantId(Integer restaurantId);

    List<Reservation> findByRestaurantIdAndStartTimeBetween(Integer restaurantId, LocalDateTime startTime, LocalDateTime startTime2);
}
