package org.galaxy.server.agent;

import org.galaxy.server.agent.dto.AgentChatResponse;
import org.galaxy.server.agent.dto.PendingAction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Component
public class AgentOrchestrator {

    private final String defaultReservationName;
    private final ZoneId zoneId;

    public AgentOrchestrator(
            @Value("${app.agent.default-name}") String defaultReservationName,
            @Value("${app.agent.timezone}") String timezone
    ) {
        this.defaultReservationName = defaultReservationName;
        this.zoneId = ZoneId.of(timezone);
    }

    public AgentChatResponse handle(String message){
        String normalized = message.toLowerCase(Locale.ROOT);

        //basic branching agentic logic
        boolean wantsReservation = normalized.contains("reservation")
                || normalized.contains("book")
                || normalized.contains("order");

        if(wantsReservation){
            return handleReservationIntent(message);
        }
        return handleSearchIntent(message);
    }

    private AgentChatResponse handleSearchIntent(String message){
        return new AgentChatResponse(
                "Sure, I can help you find a restaurant.",
                null
        );
    }

    private AgentChatResponse handleReservationIntent(String message){
        return new AgentChatResponse(
                "Sure, I can help you book a reservation. Let me find the best matching restaurant first",
                new ArrayList<PendingAction>(List.of(PendingAction.searchOnly()))
        );
    }
}
