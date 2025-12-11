package org.galaxy.server.controller;

import org.galaxy.server.loader.RestaurantDataLoader;
import org.galaxy.server.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {
    private final RestaurantDataLoader restaurantDataLoader;

    @Autowired
    public SearchController(RestaurantDataLoader restaurantDataLoader) {
        this.restaurantDataLoader = restaurantDataLoader;
    }

    @GetMapping
    public String getSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) String cuisine
    ) {
        return "Search";
    }

    @GetMapping("/restaurants")
    public String getRestaurants() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < restaurantDataLoader.getRestaurants().size(); i++)
        {
            sb.append(restaurantDataLoader.getRestaurants().get(i).toString() + "\n");
        }
        return sb.toString();
    }
}