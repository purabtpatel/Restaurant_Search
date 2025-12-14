package org.galaxy.server.model;

import java.util.Comparator;

public class RestaurantComparator implements Comparator<Restaurant> {
    @Override
    public int compare(Restaurant o1, Restaurant o2) {

        int res = Integer.compare(o1.getDistance(), o2.getDistance());
        if (res != 0) return res;

        res = Integer.compare(o2.getRating(), o1.getRating());
        if (res != 0) return res;

        res = Integer.compare(o1.getPrice(), o2.getPrice());
        if (res != 0) return res;

        return 0;
    }
}
