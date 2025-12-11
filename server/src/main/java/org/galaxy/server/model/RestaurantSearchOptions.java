package org.galaxy.server.model;

public class RestaurantSearchOptions {
    private String name;
    private Integer rating;
    private Integer distance;
    private Integer price;
    private String cuisine;

    private RestaurantSearchOptions(Builder builder) {

    }
    
    public static class Builder {

    }
}
