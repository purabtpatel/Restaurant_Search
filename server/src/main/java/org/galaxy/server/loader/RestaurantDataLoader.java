package org.galaxy.server.loader;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.galaxy.server.model.Restaurant;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestaurantDataLoader {
    private final String RESTAURANT_FILE_PATH = "restaurants.csv";
    private final String CUISINE_FILE_PATH = "cuisines.csv";

    @Getter
    private List<Restaurant> restaurants = new ArrayList<>();

    @PostConstruct
    public void loadData() {
        System.out.println("Starting data load...");
        try {
            Map<Integer, String> cuisineMap = loadCuisines();
            loadRestaurants(cuisineMap);
            System.out.println("Data load complete. Total restaurants: " + restaurants.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to load restaurant data", e);
        }
    }

    private Map<Integer, String> loadCuisines() throws IOException {
        System.out.println("Loading cuisines...");
        Map<Integer, String> map = new HashMap<>();
        ClassPathResource resource = new ClassPathResource(CUISINE_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines

                String[] parts = line.split(",");
                // ID, Name -> needs 2 columns
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    map.put(id, name);
                }
            }
        }
        System.out.println("Loaded " + map.size() + " cuisines.");
        return map;
    }

    private void loadRestaurants(Map<Integer, String> cuisineMap) throws IOException {
        System.out.println("Loading restaurants...");
        ClassPathResource resource = new ClassPathResource(RESTAURANT_FILE_PATH);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line = reader.readLine(); // skip header

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue; // skip empty lines

                String[] parts = line.split(",");

                if (parts.length >= 6) {
                    try {
                        Restaurant restaurant = new Restaurant();

                        // Parse columns based on your CSV order:
                        // id, name, customer_rating, distance, price, cuisine_id
                        restaurant.setId(Integer.parseInt(parts[0].trim()));
                        restaurant.setName(parts[1].trim());
                        restaurant.setRating(Integer.parseInt(parts[2].trim()));
                        restaurant.setDistance(Integer.parseInt(parts[3].trim()));
                        restaurant.setPrice(Integer.parseInt(parts[4].trim()));

                        int cuisineId = Integer.parseInt(parts[5].trim());
                        restaurant.setCuisineId(cuisineId);

                        // Map the ID to the actual name string
                        String cuisineName = cuisineMap.getOrDefault(cuisineId, "Unknown");
                        restaurant.setCuisine(cuisineName);

                        restaurants.add(restaurant);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid number format in line: " + line);
                    }
                } else {
                    System.err.println("Skipping malformed line (not enough columns): " + line);
                }
            }
        }
        System.out.println("Loaded " + restaurants.size() + " restaurants.");
    }
}