package org.galaxy.server.agent.dto;

import java.util.List;

/**
 * Data Transfer Object representing the AI agent's response to a user's chat request.
 */
public record AgentChatResponse (
    String reply,
    PendingAction pendingAction,
    ConversationContext context
) {}

