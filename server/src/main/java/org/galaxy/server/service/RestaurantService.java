package org.galaxy.server.service;

import org.galaxy.server.config.DataLoader;
import org.galaxy.server.model.Restaurant;
import org.galaxy.server.model.RestaurantComparator;
import org.galaxy.server.model.RestaurantSearchOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Service class for searching and managing restaurant data.
 */
@Service
public class RestaurantService {

    private final DataLoader dataLoader;

    public RestaurantService(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public List<Restaurant> advancedSearch(RestaurantSearchOptions options) {
        PriorityQueue<Restaurant> queue = new PriorityQueue<>(new RestaurantComparator());
        int limit = options.getLimit() == null ? 5 : options.getLimit();

        dataLoader.getRestaurants().stream()
                .filter(r -> options.getRating() == null || r.getRating() >= options.getRating())
                .filter(r -> options.getDistance() == null || r.getDistance() <= options.getDistance())
                .filter(r -> options.getPrice() == null || r.getPrice() <= options.getPrice())
                .filter(r -> options.getName() == null || r.getName().toLowerCase().contains(options.getName().toLowerCase()))
                .filter(r -> options.getCuisine() == null || r.getCuisine().toLowerCase().contains(options.getCuisine().toLowerCase()))
                .forEach(queue::add);

        List<Restaurant> res = new ArrayList<>();
        for(int i = 0; i < limit; i++) {
            if(!queue.isEmpty()) res.add(queue.poll());
        }
        return res;

    }

}
