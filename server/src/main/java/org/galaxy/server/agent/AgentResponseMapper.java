package org.galaxy.server.agent;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for mapping and formatting responses from the AI agent.
 */
public class AgentResponseMapper {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String formatAiExceptionMessage(String message) {
        if (message == null || message.isEmpty()) {
            return "Sorry, something went wrong. Please try again.";
        }

        try {
            // Find the start of the JSON object
            int jsonStartIndex = message.indexOf("{");
            if (jsonStartIndex != -1) {
                String jsonPart = message.substring(jsonStartIndex);
                JsonNode root = objectMapper.readTree(jsonPart);
                if (root.has("error") && root.get("error").has("message")) {
                    return root.get("error").get("message").asText();
                }
            }
        } catch (Exception e) {
            // If parsing fails, return a default or slightly cleaned up message
        }

        return "Sorry, something went wrong. Please try again.";
    }
}
