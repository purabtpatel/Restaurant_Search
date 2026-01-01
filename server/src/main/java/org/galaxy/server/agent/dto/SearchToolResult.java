package org.galaxy.server.agent.dto;

import java.util.List;

public record SearchToolResult(
        String summary,
        List<Integer> restaurantIds
) {}