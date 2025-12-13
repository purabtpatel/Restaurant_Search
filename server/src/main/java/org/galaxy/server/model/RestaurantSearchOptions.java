package org.galaxy.server.model;

import lombok.Getter;

public class RestaurantSearchOptions {
    @Getter
    private String name;
    @Getter
    private Integer rating;
    @Getter
    private Integer distance;
    @Getter
    private Integer price;
    @Getter
    private String cuisine;

    private RestaurantSearchOptions(Builder builder) {
        this.name = builder.name;
        this.rating = builder.rating;
        this.distance = builder.distance;
        this.price = builder.price;
        this.cuisine = builder.cuisine;
    }


    public static class Builder {
        private String name;
        private Integer rating;
        private Integer distance;
        private Integer price;
        private String cuisine;

        public Builder name(String name){
            this.name = name;
            return this;
        }
        public Builder rating(Integer rating){
            this.rating = rating;
            return this;
        }
        public Builder distance(Integer distance){
            this.distance = distance;
            return this;
        }
        public Builder price(Integer price){
            this.price = price;
            return this;
        }
        public Builder cuisine(String cuisine){
            this.cuisine = cuisine;
            return this;
        }
        public RestaurantSearchOptions build(){
            return new RestaurantSearchOptions(this);
        }
    }
}
