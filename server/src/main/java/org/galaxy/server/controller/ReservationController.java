package org.galaxy.server.controller;

import org.galaxy.server.model.Reservation;
import org.galaxy.server.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@CrossOrigin(origins = "http://localhost:5173")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<Reservation>> getReservations(
            @PathVariable Long restaurantId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
            ){
        try{
            List<Reservation> reservations;
            if(start == null || end == null){
                reservations = reservationService.getReservationsByRestaurantId(restaurantId);
            }else{
                 reservations = reservationService.getReservationsByRestaurantIdAndTimeBetween(restaurantId, start, end);
            }
            return ResponseEntity.ok(reservations);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(
        @RequestBody Reservation reservation
    ){
        try{
            Reservation saved = reservationService.createReservation(reservation);
            return ResponseEntity.ok(saved);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

}
