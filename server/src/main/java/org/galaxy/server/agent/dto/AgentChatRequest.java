package org.galaxy.server.agent.dto;

public record AgentChatRequest(
   String message,
   ConversationContext context
) {
    public ConversationContext contextOrEmpty() {
        return context == null ? ConversationContext.empty() : context;
    }
}
