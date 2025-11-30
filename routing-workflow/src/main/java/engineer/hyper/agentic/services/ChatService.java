package engineer.hyper.agentic.services;

import engineer.hyper.agentic.agents.RoutingWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final RoutingWorkflow routingWorkflow;

    public String processUserQuery(String input, Map<String, String> routes) {
        log.info("Received user query: {}", input);
        // Placeholder for processing logic
        String response = routingWorkflow.route(input, routes);
        log.info("Refined response: {}", response);
        return response;
    }
}
