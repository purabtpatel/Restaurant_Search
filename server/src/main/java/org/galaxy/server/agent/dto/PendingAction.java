package org.galaxy.server.agent.dto;

public record PendingAction(
        ActionType type
) {
    public static PendingAction search() {
        return new PendingAction(ActionType.SEARCH);
    }

    public static PendingAction reservation(){
        return new PendingAction(ActionType.PENDING_RESERVATION);
    }

}



//public record PendingAction(
//        ActionType type,
//        Integer restaurantId,
//        String restaurantName,
//        LocalDateTime timestamp
//){}
