package org.galaxy.server.agent.dto;

/**
 * Data Transfer Object representing a user's chat request to the AI agent.
 */
public record AgentChatRequest(
   String message,
   ConversationContext context
) {
    public ConversationContext contextOrEmpty() {
        return context == null ? ConversationContext.empty() : context;
    }
}
