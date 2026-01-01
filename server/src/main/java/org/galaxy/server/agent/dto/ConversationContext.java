package org.galaxy.server.agent.dto;

import java.util.List;

public record ConversationContext(
        AgentIntent lastIntent,
        List<Integer> lastRestaurantIds,
        PendingAction pendingAction
) {
    public static ConversationContext empty() {
        return new ConversationContext(null, null, null);
    }
}
