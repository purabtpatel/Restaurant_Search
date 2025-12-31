package org.galaxy.server.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class RestaurantSearchOptions {
    private String name;
    private Integer rating;
    private Integer distance;
    private Integer price;
    private String cuisine;
    private Integer limit;
}
