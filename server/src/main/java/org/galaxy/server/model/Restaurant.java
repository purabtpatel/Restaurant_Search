package org.galaxy.server.model;

import lombok.Data;

@Data
public class Restaurant{
    private String name;
    private Integer rating;
    private Integer distance;
    private Integer price;
    private Integer cuisineId;
    private String cuisine;

    public Restaurant() {}
    public Restaurant(String name, Integer rating, Integer distance, Integer price, Integer cuisineId, String cuisine) {
        this.name = name;
        this.rating = rating;
        this.distance = distance;
        this.price = price;
        this.cuisineId = cuisineId;
        this.cuisine = cuisine;
    }

    public String toString() {
        return "Name: " + name +
            ", Rating: " + rating +
            ", Distance: " + distance +
            ", Price: " + price +
            ", Cuisine: " + cuisine;
    }
}
