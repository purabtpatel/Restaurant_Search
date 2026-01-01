package org.galaxy.server.agent.dto;

import java.util.List;

public record AgentChatResponse (
    String reply,
    PendingAction pendingAction,
    ConversationContext context
) {}

