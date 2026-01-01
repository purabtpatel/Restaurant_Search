package org.galaxy.server.agent;

import org.galaxy.server.agent.dto.*;
import org.galaxy.server.agent.tools.RestaurantSearchTool;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Objects;

/**
 * Orchestrates the interaction between the user, the AI chat model, and various tools.
 * It classifies user intent and delegates the request to the appropriate handler.
 */
@Component
public class AgentOrchestrator {

    private final ChatModel chatModel;
    private final RestaurantSearchTool restaurantSearchTool;
    private final String defaultReservationName;
    private final ZoneId zoneId;

    public AgentOrchestrator(
            ChatModel chatModel,
            RestaurantSearchTool restaurantSearchTool,
            @Value("${app.agent.default-name}") String defaultReservationName,
            @Value("${app.agent.timezone}") String timezone
    ) {
        this.chatModel = chatModel;
        this.restaurantSearchTool = restaurantSearchTool;
        this.defaultReservationName = defaultReservationName;
        this.zoneId = ZoneId.of(timezone);
    }

    public AgentChatResponse handle(String message, ConversationContext context) {
        try {
            AgentIntent intent = classifyIntent(message);

            return switch (intent) {
                case SEARCH -> handleSearchIntent(message, context);
                case RESERVE -> handleReservationIntent(message, context);
            };

        } catch (IllegalArgumentException e) {
            return new AgentChatResponse(
                    "Sorry, I could not understand your request. Please try again.",
                    null,
                    context
            );
        } catch (NonTransientAiException e) {
            return new AgentChatResponse(
                    AgentResponseMapper.formatAiExceptionMessage(e.getMessage()),
                    null,
                    context
            );
        } catch (Exception e) {
            return new AgentChatResponse(
                    "Sorry, something went wrong. Please try again.",
                    null,
                    context
            );
        }
    }

    private AgentIntent classifyIntent(String message) {
        String systemPrompt = String.format("""
            You are a restaurant assistant.
            Determine whether the user wants to:
            - search for restaurants
            - make a reservation

            User input: %s

            Respond with exactly one word:
            SEARCH or RESERVE
            """, message);

        ChatResponse response = chatModel.call(
                new Prompt(
                        systemPrompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .maxTokens(10)
                                .build()
                )
        );

        String raw = Objects.requireNonNull(
                response.getResult().getOutput().getText()
        ).trim().toUpperCase();

        return switch (raw) {
            case "SEARCH" -> AgentIntent.SEARCH;
            case "RESERVE" -> AgentIntent.RESERVE;
            default -> throw new IllegalArgumentException("Unknown intent: " + raw);
        };
    }

    private AgentChatResponse handleSearchIntent(String message, ConversationContext context) {
        SearchToolResult result = restaurantSearchTool.search(message);

        ConversationContext updatedContext = new ConversationContext(
                AgentIntent.SEARCH,
                result.restaurantIds(),
                PendingAction.search()
        );

        return new AgentChatResponse(
                result.summary(),
                PendingAction.search(),
                updatedContext
        );
    }

    private AgentChatResponse handleReservationIntent(String message, ConversationContext context) {
        ConversationContext updatedContext = new ConversationContext(
                AgentIntent.RESERVE,
                context.lastRestaurantIds(),
                PendingAction.reservation()
        );

        return new AgentChatResponse(
                "Sure, I can help you book a reservation. Let me find the best matching restaurant first.",
                PendingAction.reservation(),
                updatedContext
        );
    }
}
