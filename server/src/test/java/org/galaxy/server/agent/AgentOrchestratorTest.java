package org.galaxy.server.agent;

import org.galaxy.server.agent.dto.AgentChatResponse;
import org.galaxy.server.agent.dto.ActionType;
import org.galaxy.server.agent.tools.RestaurantSearchTool;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.retry.NonTransientAiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentOrchestratorTest {

    @Test
    void handle_NonTransientAiException_ReturnsFormattedMessage() {
        ChatModel chatModel = mock(ChatModel.class);
        RestaurantSearchTool searchTool = mock(RestaurantSearchTool.class);
        AgentOrchestrator orchestrator = new AgentOrchestrator(chatModel, searchTool, "Default", "UTC");

        String rawErrorMessage = "HTTP 401 - {\n" +
                "    \"error\": {\n" +
                "        \"message\": \"Incorrect API key provided: dcs. You can find your API key at https://platform.openai.com/account/api-keys.\",\n" +
                "        \"type\": \"invalid_request_error\",\n" +
                "        \"param\": null,\n" +
                "        \"code\": \"invalid_api_key\"\n" +
                "    }\n" +
                "}";
        
        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class))).thenThrow(new NonTransientAiException(rawErrorMessage));

        AgentChatResponse response = orchestrator.handle("hello", null);

        assertEquals("Incorrect API key provided: dcs. You can find your API key at https://platform.openai.com/account/api-keys.", response.reply());
    }

    @Test
    void handle_SearchIntent_CallsSearchTool() {
        ChatModel chatModel = mock(ChatModel.class);
        RestaurantSearchTool searchTool = mock(RestaurantSearchTool.class);
        AgentOrchestrator orchestrator = new AgentOrchestrator(chatModel, searchTool, "Default", "UTC");

        ChatResponse chatResponse = mock(ChatResponse.class);
        Generation generation = new Generation(new AssistantMessage("SEARCH"));
        when(chatResponse.getResult()).thenReturn(generation);
        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class))).thenReturn(chatResponse);

        String message = "find me a cheap Italian place";
        when(searchTool.search(message)).thenReturn("I found some restaurants");

        AgentChatResponse response = orchestrator.handle(message, null);

        assertEquals("I found some restaurants", response.reply());
        assertNotNull(response.pendingAction());
        assertEquals(ActionType.SEARCH, response.pendingAction().type());
    }
}
