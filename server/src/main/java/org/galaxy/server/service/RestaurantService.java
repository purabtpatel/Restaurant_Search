package org.galaxy.server.service;

import org.galaxy.server.loader.RestaurantDataLoader;
import org.galaxy.server.model.Restaurant;
import org.galaxy.server.model.RestaurantSearchOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantDataLoader restaurantDataLoader;

    public RestaurantService(RestaurantDataLoader restaurantDataLoader) {
        this.restaurantDataLoader = restaurantDataLoader;
    }

    public List<Restaurant> basicSearch(RestaurantSearchOptions options) {

        return restaurantDataLoader.getRestaurants().stream()
                .filter(r -> options.getDistance() == null || r.getDistance().equals(options.getDistance()))
                .filter(r -> options.getRating() == null || r.getRating().equals(options.getRating()))
                .filter(r -> options.getPrice() == null || r.getPrice().equals(options.getPrice()))
                .filter(r -> options.getCuisine() == null || r.getCuisine().equals(options.getCuisine()))
                .filter(r -> options.getName() == null || r.getName().equalsIgnoreCase(options.getName()))
                .toList();
    }

    public List<Restaurant> advancedSearch(RestaurantSearchOptions options) {

        return restaurantDataLoader.getRestaurants().stream()
                .filter(r -> options.getRating() == null || r.getRating() >= options.getRating())
                .filter(r -> options.getDistance() == null || r.getDistance() <= options.getDistance())
                .filter(r -> options.getPrice() == null || r.getPrice() <= options.getPrice())
                .filter(r -> options.getName() == null || r.getName().contains(options.getName()))
                .filter(r -> options.getCuisine() == null || r.getCuisine().contains(options.getCuisine()))
                .toList();
    }



}
