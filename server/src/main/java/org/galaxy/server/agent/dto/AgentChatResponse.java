package org.galaxy.server.agent.dto;

import java.util.List;

public record AgentChatResponse (
    String reply,
    List<PendingAction> actions
) {}

