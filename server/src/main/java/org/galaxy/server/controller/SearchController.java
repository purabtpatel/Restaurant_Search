package org.galaxy.server.controller;

import org.galaxy.server.model.Restaurant;
import org.galaxy.server.model.RestaurantSearchOptions;
import org.galaxy.server.service.RestaurantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
@CrossOrigin(origins = "http://localhost:5173")
public class SearchController {

    private final RestaurantService restaurantService;

    public SearchController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping("/basic")
    public ResponseEntity<List<Restaurant>> getBasicSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String cuisine
    ) {
        try{
            RestaurantSearchOptions inputOptions = new RestaurantSearchOptions.Builder()
                    .name(name)
                    .rating(rating)
                    .distance(distance)
                    .price(price)
                    .cuisine(cuisine)
                    .build();
            List<Restaurant> results = restaurantService.basicSearch(inputOptions);
            return ResponseEntity.ok(results);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/advanced")
    public ResponseEntity<List<Restaurant>> getAdvancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String cuisine,
            @RequestParam(required = false) Integer limit)
    {
        try{
            RestaurantSearchOptions inputOptions = new RestaurantSearchOptions.Builder()
                    .name(name)
                    .rating(rating)
                    .distance(distance)
                    .price(price)
                    .cuisine(cuisine)
                    .limit(limit)
                    .build();
            List<Restaurant> results = restaurantService.advancedSearch(inputOptions);
            return ResponseEntity.ok(results);
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }
}