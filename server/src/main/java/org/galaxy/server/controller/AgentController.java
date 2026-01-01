package org.galaxy.server.controller;

import org.galaxy.server.agent.AgentOrchestrator;
import org.galaxy.server.agent.dto.AgentChatRequest;
import org.galaxy.server.agent.dto.AgentChatResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling chat interactions with the restaurant assistant agent.
 */
@RestController
@RequestMapping("/agents")
public class AgentController {
    private final AgentOrchestrator agentOrchestrator;

    public AgentController(AgentOrchestrator agentOrchestrator){
        this.agentOrchestrator = agentOrchestrator;

    }

    @PostMapping("/chat")
    public AgentChatResponse chat(@RequestBody AgentChatRequest request){
        return agentOrchestrator.handle(
                request.message(),
                request.contextOrEmpty()
        );

    }
}
