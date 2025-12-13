package org.galaxy.server.controller;

import org.galaxy.server.model.RestaurantSearchOptions;
import org.galaxy.server.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final RestaurantService restaurantService;

    @Autowired
    public SearchController(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @GetMapping
    @RequestMapping("/basic")
    public String getBasicSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String cuisine
    ) {

        RestaurantSearchOptions inputOptions = new RestaurantSearchOptions.Builder()
                .name(name)
                .rating(rating)
                .distance(distance)
                .price(price)
                .cuisine(cuisine)
                .build();
        return restaurantService.basicSearch(inputOptions).toString();
    }

    @GetMapping
    @RequestMapping("/advanced")
    public String getAdvancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String cuisine)
    {
        RestaurantSearchOptions inputOptions = new RestaurantSearchOptions.Builder()
                .name(name)
                .rating(rating)
                .distance(distance)
                .price(price)
                .cuisine(cuisine)
                .build();
        return restaurantService.advancedSearch(inputOptions).toString();
    }
}