package org.galaxy.server.service;

import org.galaxy.server.loader.RestaurantDataLoader;
import org.galaxy.server.model.Restaurant;
import org.galaxy.server.model.RestaurantSearchOptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @Mock
    private RestaurantDataLoader restaurantDataLoader;

    @InjectMocks
    private RestaurantService restaurantService;

    private List<Restaurant> mockRestaurants;

    @BeforeEach
    void setUp() {
        mockRestaurants = Arrays.asList(
                new Restaurant("Deliciousgenix", 4, 1, 10, 11, "Spanish"),
                new Restaurant("Cuts Delicious", 3, 9, 25, 8, "Korean"),
                new Restaurant("Fine Delicious", 4, 5, 45, 4, "Italian"),
                new Restaurant("Local Delicious", 5, 4, 20, 12, "Greek"),
                new Restaurant("Deliciouszilla", 4, 1, 15, 2, "Chinese"),
                new Restaurant("Wish Chow", 3, 1, 40, 1, "American")
        );

        when(restaurantDataLoader.getRestaurants()).thenReturn(mockRestaurants);
    }

    @Nested
    class BasicSearchTests {

        @Test
        void testSearchByNameExactMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .name("deliciousgenix")
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(1, results.size());
            assertEquals("Deliciousgenix", results.get(0).getName());
        }

        @Test
        void testSearchByNamePartialMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .name("Delicious")
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertTrue(results.isEmpty());
        }

        @Test
        void testSearchByRatingExactMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(5)
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(1, results.size());
            assertEquals("Local Delicious", results.get(0).getName());
        }

        @Test
        void testSearchByRatingNoMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(1)
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertTrue(results.isEmpty());
        }

        @Test
        void testSearchByMultipleCriteria() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(4)
                    .price(15)
                    .cuisine("Chinese")
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(1, results.size());
            assertEquals("Deliciouszilla", results.get(0).getName());
        }

        @Test
        void testSearchWithEmptyOptions() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(mockRestaurants.size(), results.size());
        }
    }
}