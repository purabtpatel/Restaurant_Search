package org.galaxy.server.service;

import org.galaxy.server.loader.DataLoader;
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
    private DataLoader dataLoader;

    @InjectMocks
    private RestaurantService restaurantService;

    private List<Restaurant> mockRestaurants;

    @BeforeEach
    void setUp() {
        // Define mock data that includes clear tie-breaker scenarios for advanced search validation.
        mockRestaurants = Arrays.asList(
                // R1: Distance 1, Rating 4, Price 10, Cuisine Spanish
                new Restaurant(1, "Deliciousgenix", 4, 1, 10, 11, "Spanish"),
                // R2: Distance 9, Rating 3, Price 25, Cuisine Korean
                new Restaurant(2, "Cuts Delicious", 3, 9, 25, 8, "Korean"),
                // R3: Distance 5, Rating 4, Price 45, Cuisine Italian
                new Restaurant(3, "Fine Delicious", 4, 5, 45, 4, "Italian"),
                // R4: Distance 4, Rating 5, Price 20, Cuisine Greek
                new Restaurant(4, "Local Delicious", 5, 4, 20, 12, "Greek"),
                // R5: Distance 1, Rating 4, Price 15, Cuisine Chinese (Tie-breaker for R1: same D/R, higher P)
                new Restaurant(5, "Deliciouszilla", 4, 1, 15, 2, "Chinese"),
                // R6: Distance 1, Rating 3, Price 40, Cuisine American
                new Restaurant(6, "Wish Chow", 3, 1, 40, 1, "American")
        );

        when(dataLoader.getRestaurants()).thenReturn(mockRestaurants);
    }

    @Nested
    class BasicSearchTests {

        // --- Basic Search Tests (Verify simple filtering logic without sorting) ---

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
            // Test case assumes basicSearch uses exact matching, so a partial name should return nothing.
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
            // Should be no results since no restaurant has a rating of 1.
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
            assertEquals("Cuts Delicious", results.get(0).getName());
        }

        @Test
        void testSearchByDistanceMultipleMatches() {
            // Tests that the filter correctly applies a maximum distance (<= 1).
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
            // Ensures the distance filter returns nothing when the criteria is too restrictive.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(99)
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertTrue(results.isEmpty());
        }

        @Test
        void testSearchByMultipleCriteria() {
            // Verifies the AND logic of combined filters.
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
            // Verifies that a search with no criteria returns all restaurants.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .build();

            List<Restaurant> results = restaurantService.basicSearch(options);

            assertEquals(mockRestaurants.size(), results.size());
        }
    }

    @Nested
    class AdvancedSearchTests {

        /**
         * Utility to assert the exact priority order of the resulting list.
         * This simplifies complex sorting assertions across multiple test cases.
         */
        private void assertOrder(List<Restaurant> results, String... expectedNames) {
            assertEquals(expectedNames.length, results.size(), "Result size mismatch");
            for (int i = 0; i < expectedNames.length; i++) {
                assertEquals(expectedNames[i], results.get(i).getName(), "Element at index " + i + " is incorrect.");
            }
        }

        // --- Sorting and Default Limit Tests ---

        @Test
        void testSortByDistancePrimaryCriterion_Limit5Applied() {
            // Verifies the primary sort rule (Distance ASC).
            // ASSUMPTION: The production code applies a default limit of 5 when none is set,
            // which should exclude the lowest priority restaurant (Cuts Delicious, D=9).
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder().build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(5, results.size(), "Must return 5 elements due to implicit limit.");

            // Assert that the distance hierarchy (1 < 4 < 5) is maintained.
            assertEquals(1, results.get(0).getDistance(), "First item must be the minimum distance.");
            assertEquals(4, results.get(3).getDistance(), "Fourth item distance must be 4 (Local Delicious).");
            assertEquals(5, results.get(4).getDistance(), "Fifth item distance must be 5 (Fine Delicious).");

            assertOrder(results, "Deliciousgenix", "Deliciouszilla", "Wish Chow", "Local Delicious", "Fine Delicious");
        }

        @Test
        void testSortByRatingSecondaryCriterion_NoLimitImpact() {
            // Verifies the secondary sort rule (Rating DESC) when Distance is tied (D=1).
            // Since only 3 candidates match, the default limit is irrelevant.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(1)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(3, results.size());
            // Assert R=4 comes before R=3
            assertEquals(4, results.get(0).getRating());
            assertEquals(4, results.get(1).getRating());
            assertEquals(3, results.get(2).getRating());
        }

        @Test
        void testSortByPriceTertiaryCriterion_TieBreaker() {
            // Verifies the tertiary sort rule (Price ASC) when both Distance and Rating are tied.
            // Candidates: R1 (P=10) and R5 (P=15).
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(1)
                    .rating(4)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            // R1 must win the tie-breaker because of lower price.
            assertOrder(results, "Deliciousgenix", "Deliciouszilla");
        }

        @Test
        void testCombinedSortScenario_Limit5Applied() {
            // Verifies the full multi-criteria sort hierarchy on a filtered list (Distance <= 5).
            // Since this returns exactly 5 candidates, the implicit limit is satisfied.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(5)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(5, results.size());
            assertOrder(results, "Deliciousgenix", "Deliciouszilla", "Wish Chow", "Local Delicious", "Fine Delicious");
        }

        // --- Filtering and Explicit Limiting Tests ---

        @Test
        void testSearchCombinedCriteriaWithExplicitLimit() {
            // Verifies that explicit limits override the default limit of 5.
            // Candidates: R1, R5, R4 (3 matches total). Limit is 2.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(4)
                    .price(20)
                    .limit(2)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(2, results.size(), "The result size must be restricted by the explicit limit.");
            // R1 and R5 are the top two matches.
            assertOrder(results, "Deliciousgenix", "Deliciouszilla");
        }

        @Test
        void testLimitExceedsMatchesReturnsAllMatches() {
            // Ensures that if the explicit limit is higher than the number of matches, all matches are returned.
            // Candidates: R1, R5, R6 (3 matches). Limit is 10.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .distance(1)
                    .limit(10)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(3, results.size());
            assertOrder(results, "Deliciousgenix", "Deliciouszilla", "Wish Chow");
        }

        @Test
        void testSearchWithEmptyOptionsAndLimit() {
            // Verifies explicit limiting works on the full dataset (6 restaurants).
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .limit(3)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            // Should return the top 3 (R1, R5, R6) based on the Comparator.
            assertEquals(3, results.size());
            assertOrder(results, "Deliciousgenix", "Deliciouszilla", "Wish Chow");
        }

        @Test
        void testSearchReturnsEmptyWhenNoMatches() {
            // Ensures that multiple filters leading to zero candidates results in an empty list.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .rating(5)
                    .price(1)
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertTrue(results.isEmpty());
        }

        @Test
        void testSearchWithEmptyOptionsReturnsDefaultLimit() {
            // Confirms the default limit of 5 is enforced when no criteria or limit is specified.
            RestaurantSearchOptions options = new RestaurantSearchOptions.Builder()
                    .build();

            List<Restaurant> results = restaurantService.advancedSearch(options);

            assertEquals(5, results.size());
            assertOrder(results, "Deliciousgenix", "Deliciouszilla", "Wish Chow", "Local Delicious", "Fine Delicious");
        }
    }
}