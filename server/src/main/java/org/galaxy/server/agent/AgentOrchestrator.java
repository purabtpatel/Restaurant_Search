package org.galaxy.server.agent;

import org.galaxy.server.agent.dto.AgentChatResponse;
import org.galaxy.server.agent.dto.PendingAction;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class AgentOrchestrator {
    private final ChatModel chatModel;
    private final String defaultReservationName;
    private final ZoneId zoneId;

    public AgentOrchestrator(
            ChatModel chatModel,
            @Value("${app.agent.default-name}") String defaultReservationName,
            @Value("${app.agent.timezone}") String timezone
    ) {
        this.chatModel = chatModel;
        this.defaultReservationName = defaultReservationName;
        this.zoneId = ZoneId.of(timezone);
    }

    public AgentChatResponse handle(String message){
        String systemPrompt = """
        You are a restaurant assistant.
        Determine whether the user wants to:
        - search for restaurants
        - make a reservation

        Respond with exactly one word:
        SEARCH or RESERVE
        """;

        ChatResponse response = chatModel.call(
                new Prompt(
                        systemPrompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .maxTokens(150)
                                .build()
                ));

        return new AgentChatResponse(
                "I will help you book a reservation. Let me find the best matching restaurant.",
                PendingAction.searchOnly()
        );
    }

    private AgentChatResponse handleSearchIntent(String message){
        return new AgentChatResponse(
                "Sure, I can help you find a restaurant.",
                null
        );
    }

    private AgentChatResponse handleReservationIntent(String message){
        return new AgentChatResponse(
                "Sure, I can help you book a reservation. Let me find the best matching restaurant first",
                PendingAction.searchOnly()
        );
    }
}
