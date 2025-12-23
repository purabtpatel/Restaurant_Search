package org.galaxy.server.loader;

import org.galaxy.server.model.Restaurant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataLoaderTest {

    @Test
    @DisplayName("Should load real CSV files and map relationships correctly")
    void testLoadData_Integration() {
        // Arrange
        DataLoader loader = new DataLoader();

        // Act
        loader.loadData();
        List<Restaurant> results = loader.getRestaurants();

        // Assert 1: Data exists
        assertFalse(results.isEmpty(), "Restaurant list should not be empty");

        // Assert 2: Verify specific data integrity (based on your CSV provided)
        // CSV: Deliciousgenix,4,1,10,11
        // Cuisine 11 is "Spanish"
        Restaurant firstRestaurant = results.stream()
                .filter(r -> r.getName().equals("Deliciousgenix"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Deliciousgenix not found"));

        assertEquals(4, firstRestaurant.getRating());
        assertEquals(11, firstRestaurant.getCuisineId());
        assertEquals("Spanish", firstRestaurant.getCuisine(), "Cuisine ID 11 should map to 'Spanish'");
    }
}