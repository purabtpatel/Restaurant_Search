package org.galaxy.server.service;

import org.galaxy.server.loader.RestaurantDataLoader;
import org.galaxy.server.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    public RestaurantService(RestaurantDataLoader restaurantDataLoader) {
    }

//    public List<Restaurant>


}
