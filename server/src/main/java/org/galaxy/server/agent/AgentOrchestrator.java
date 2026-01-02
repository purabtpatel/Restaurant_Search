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
            ConversationContext safeContext =
                    context == null ? ConversationContext.empty() : context;

            if (safeContext.pendingAction() != null) {
                ConfirmationIntent confirmation =
                        classifyConfirmation(message, safeContext);

                if (confirmation != ConfirmationIntent.UNKNOWN) {
                    return handleConfirmation(confirmation, message, safeContext);
                }
            }

            AgentIntent intent = classifyIntent(message);

            return switch (intent) {
                case SEARCH -> handleSearchIntent(message, safeContext);
                case RESERVE -> handleReservationIntent(message, safeContext);
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

    /* ============================
       Intent Classification
       ============================ */

    private AgentIntent classifyIntent(String message) {
        String systemPrompt = String.format("""
            You are a restaurant assistant.
            Determine whether the user wants to:
            - search for restaurants
            - make a reservation

            User input:
            %s

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

        return AgentIntent.valueOf(raw);
    }

    private ConfirmationIntent classifyConfirmation(
            String message,
            ConversationContext context
    ) {
        String systemPrompt = String.format("""
            You are interpreting a follow-up after a restaurant interaction.

            The user may:
            - confirm they want to proceed
            - reject the current action
            - select a specific restaurant
            - continue searching or refining

            User message:
            %s

            Respond with exactly ONE word:
            CONFIRM, REJECT, SELECT, CONTINUE, or UNKNOWN
            """, message);

        ChatResponse response = chatModel.call(
                new Prompt(
                        systemPrompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .maxTokens(5)
                                .build()
                )
        );

        String raw = Objects.requireNonNull(
                response.getResult().getOutput().getText()
        ).trim().toUpperCase();

        return ConfirmationIntent.valueOf(raw);
    }

    /* ============================
       Intent Handlers
       ============================ */

    private AgentChatResponse handleSearchIntent(
            String message,
            ConversationContext context
    ) {
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

    private AgentChatResponse handleReservationIntent(
            String message,
            ConversationContext context
    ) {
        ConversationContext updatedContext = new ConversationContext(
                AgentIntent.RESERVE,
                context.lastRestaurantIds(),
                PendingAction.reservation()
        );

        return new AgentChatResponse(
                "Sure, I can help you book a reservation. Which restaurant would you like?",
                PendingAction.reservation(),
                updatedContext
        );
    }

    /* ============================
       Confirmation Handling
       ============================ */

    private AgentChatResponse handleConfirmation(
            ConfirmationIntent confirmation,
            String message,
            ConversationContext context
    ) {
        return switch (confirmation) {

            case REJECT -> new AgentChatResponse(
                    "Okay, let me know how else I can help.",
                    null,
                    ConversationContext.empty()
            );

            case CONTINUE -> handleSearchIntent(message, context);

            case SELECT -> new AgentChatResponse(
                    "Got it. I will use that restaurant.",
                    PendingAction.reservation(),
                    new ConversationContext(
                            AgentIntent.RESERVE,
                            context.lastRestaurantIds(),
                            PendingAction.reservation()
                    )
            );

            case CONFIRM -> switch (context.pendingAction().type()) {

                case SEARCH -> new AgentChatResponse(
                        "Great. Which restaurant would you like to book?",
                        PendingAction.reservation(),
                        new ConversationContext(
                                AgentIntent.RESERVE,
                                context.lastRestaurantIds(),
                                PendingAction.reservation()
                        )
                );

                case PENDING_RESERVATION -> new AgentChatResponse(
                        "Perfect. I will proceed with the reservation.",
                        PendingAction.reservation(),
                        context
                );
            };

            case UNKNOWN -> throw new IllegalStateException("Unexpected UNKNOWN confirmation");
        };
    }
}
