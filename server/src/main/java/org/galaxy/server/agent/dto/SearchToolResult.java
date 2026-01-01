package org.galaxy.server.agent.dto;

import java.util.List;

/**
 * Data Transfer Object representing the result of a restaurant search tool execution.
 */
public record SearchToolResult(
        String summary,
        List<Integer> restaurantIds
) {}