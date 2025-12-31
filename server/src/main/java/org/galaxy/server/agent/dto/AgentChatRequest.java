package org.galaxy.server.agent.dto;

public record AgentChatRequest(
   String message,
   Boolean confirmed
) {}
