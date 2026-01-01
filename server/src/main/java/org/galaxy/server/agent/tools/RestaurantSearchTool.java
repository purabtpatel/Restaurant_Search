package org.galaxy.server.agent.tools;

import org.galaxy.server.agent.dto.SearchToolRequest;
import org.galaxy.server.agent.dto.SearchToolResult;
import org.galaxy.server.model.Restaurant;
import org.galaxy.server.model.RestaurantSearchOptions;
import org.galaxy.server.service.RestaurantService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class RestaurantSearchTool {

    private final RestaurantService restaurantService;
    private final ChatModel chatModel;

    public RestaurantSearchTool(RestaurantService restaurantService, ChatModel chatModel) {
        this.restaurantService = restaurantService;
        this.chatModel = chatModel;
    }

    public SearchToolResult search(String message) {
        SearchToolRequest searchRequest = extractSearchRequest(message);
        
        RestaurantSearchOptions options = RestaurantSearchOptions.builder()
                .name(searchRequest.getName())
                .rating(searchRequest.getRating())
                .distance(searchRequest.getDistance())
                .price(searchRequest.getPrice())
                .cuisine(searchRequest.getCuisine())
                .limit(searchRequest.getLimit())
                .build();

        List<Restaurant> restaurants = restaurantService.advancedSearch(options);

        List<Integer> ids = restaurants.stream()
                .map(Restaurant::getId)
                .toList();

        String format = format(restaurants);

        return new SearchToolResult(format, ids);
    }

    private SearchToolRequest extractSearchRequest(String message) {
        var outputConverter = new BeanOutputConverter<>(SearchToolRequest.class);

        String format = outputConverter.getFormat();
        String systemPrompt = String.format("""
                Extract restaurant search parameters from the user message.
                If a parameter is not mentioned, leave it null.
                
                %s
                
                User message: %s
                """, format, message);

        var response = chatModel.call(new Prompt(systemPrompt, 
                OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .build()));

        return outputConverter.convert(Objects.requireNonNull(response.getResult().getOutput().getText()));
    }

    public String format(List<Restaurant> restaurants) {
        if (restaurants.isEmpty()) {
            return "I couldn't find any restaurants matching your criteria.";
        }
        return "I found the following restaurants for you:\n" + restaurants.stream()
                .map(r -> String.format("- %s (Rating: %d, Distance: %d, Price: %d, Cuisine: %s)",
                        r.getName(), r.getRating(), r.getDistance(), r.getPrice(), r.getCuisine()))
                .collect(Collectors.joining("\n"));
    }
}
