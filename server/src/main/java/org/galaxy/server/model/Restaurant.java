package org.galaxy.server.model;

import lombok.Data;

@Data
public class Restaurant {
    private String name;
    private Integer rating;
    private Integer distance;
    private Integer price;
    private Integer cuisineId;
    private String cuisine;

    public String toString() {
        return "Name: " + name +
            ", Rating: " + rating +
            ", Distance: " + distance +
            ", Price: " + price +
            ", Cuisine: " + cuisine;
    }
}
