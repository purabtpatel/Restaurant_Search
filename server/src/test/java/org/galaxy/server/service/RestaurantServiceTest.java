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
            assertEquals("Deliciousgenix", results.getFirst().getName());
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
            assertEquals("Local Delicious", results.getFirst().getName());
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
        void testSearchByDistanceExactMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(9)
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(1, results.size());
            assertEquals("Cuts Delicious", results.getFirst().getName());
        }

        @Test
        void testSearchByDistanceMultipleMatches() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(1)
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(3, results.size());
            assertTrue(results.stream().anyMatch(r -> r.getName().equals("Deliciousgenix")));
            assertTrue(results.stream().anyMatch(r -> r.getName().equals("Deliciouszilla")));
            assertTrue(results.stream().anyMatch(r -> r.getName().equals("Wish Chow")));
        }

        @Test
        void testSearchByDistanceNoMatch() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(99)
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
            assertEquals("Deliciouszilla", results.getFirst().getName());
        }

        @Test
        void testSearchWithEmptyOptions() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(mockRestaurants.size(), results.size());
        }
    }

    @Nested
    class AdvancedSearchTests {

        @Test
        void testSearchByNamePartialMatch() {
            // Should match "Deliciousgenix", "Cuts Delicious", "Fine Delicious", "Local Delicious", "Deliciouszilla"
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .name("Delicious")
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(5, results.size());
            assertTrue(results.stream().noneMatch(r -> r.getName().equals("Wish Chow")));
        }

        @Test
        void testSearchByRatingGreaterOrEqual() {
            // Logic: >= 4
            // Matches: 4, 4, 5, 4 (Deliciousgenix, Fine Delicious, Local Delicious, Deliciouszilla)
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(4)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(4, results.size());
            assertTrue(results.stream().allMatch(r -> r.getRating() >= 4));
        }

        @Test
        void testSearchByDistanceLessOrEqual() {
            // Logic: <= 5
            // Matches: 1, 5, 4, 1, 1 (All except "Cuts Delicious" which is 9)
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(5)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(5, results.size());
            assertTrue(results.stream().noneMatch(r -> r.getName().equals("Cuts Delicious")));
        }

        @Test
        void testSearchByPriceLessOrEqual() {
            // Logic: <= 20
            // Matches: 10, 20, 15 (Deliciousgenix, Local Delicious, Deliciouszilla)
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .price(20)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(3, results.size());
            assertTrue(results.stream().allMatch(r -> r.getPrice() <= 20));
        }

        @Test
        void testSearchByCuisinePartialMatch() {
            // Logic: contains "an"
            // Matches: Sp(an)ish, Kore(an), Itali(an), Americ(an)
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .cuisine("an")
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(4, results.size());
            assertTrue(results.stream().anyMatch(r -> r.getCuisine().equals("Spanish")));
            assertTrue(results.stream().anyMatch(r -> r.getCuisine().equals("American")));
        }

        @Test
        void testSearchCombinedCriteria() {
            // Filter: Rating >= 4 AND Price <= 20
            // Candidates (Rating >= 4): Deliciousgenix (10), Fine Delicious (45), Local Delicious (20), Deliciouszilla (15)
            // Apply Price <= 20: Deliciousgenix, Local Delicious, Deliciouszilla
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(4)
                    .price(20)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(3, results.size());
            assertTrue(results.stream().noneMatch(r -> r.getName().equals("Fine Delicious")));
        }

        @Test
        void testSearchReturnsEmptyWhenNoMatches() {
            // Logic: Rating >= 5 (Local Delicious) AND Price <= 10 (Deliciousgenix)
            // Intersection is empty
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(5)
                    .price(10)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertTrue(results.isEmpty());
        }

        @Test
        void testSearchWithEmptyOptionsReturnsAll() {
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(mockRestaurants.size(), results.size());
        }
    }
}