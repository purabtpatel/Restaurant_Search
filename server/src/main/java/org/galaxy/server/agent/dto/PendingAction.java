package org.galaxy.server.agent.dto;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

public record PendingAction(
        ActionType type
) {
    public static PendingAction searchOnly() {
        return new PendingAction(ActionType.SEARCH);
    }
}



//public record PendingAction(
//        ActionType type,
//        Long restaurantId,
//        String restaurantName,
//        LocalDateTime timestamp
//){}
