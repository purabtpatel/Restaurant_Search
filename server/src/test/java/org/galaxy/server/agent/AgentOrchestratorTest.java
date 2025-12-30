package org.galaxy.server.agent;

import org.galaxy.server.agent.dto.AgentChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.retry.NonTransientAiException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AgentOrchestratorTest {

    @Test
    void handle_NonTransientAiException_ReturnsFormattedMessage() {
        ChatModel chatModel = mock(ChatModel.class);
        AgentOrchestrator orchestrator = new AgentOrchestrator(chatModel, "Default", "UTC");

        String rawErrorMessage = "HTTP 401 - {\n" +
                "    \"error\": {\n" +
                "        \"message\": \"Incorrect API key provided: dcs. You can find your API key at https://platform.openai.com/account/api-keys.\",\n" +
                "        \"type\": \"invalid_request_error\",\n" +
                "        \"param\": null,\n" +
                "        \"code\": \"invalid_api_key\"\n" +
                "    }\n" +
                "}";
        
        when(chatModel.call(any(org.springframework.ai.chat.prompt.Prompt.class))).thenThrow(new NonTransientAiException(rawErrorMessage));

        AgentChatResponse response = orchestrator.handle("hello");

        assertEquals("Incorrect API key provided: dcs. You can find your API key at https://platform.openai.com/account/api-keys.", response.reply());
    }
}
