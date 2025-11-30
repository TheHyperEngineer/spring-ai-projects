package engineer.hyper.agentic.services;

import engineer.hyper.agentic.agents.OrchestratorWorkers;
import engineer.hyper.agentic.records.FinalResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final OrchestratorWorkers orchestratorWorkers;

    public FinalResponse processUserQuery(String query) {
        log.info("Received user query: {}", query);
        // Placeholder for processing logic
        FinalResponse finalResponse = orchestratorWorkers.process(query);
        log.info("Refined response: {}", finalResponse.analysis());
        return finalResponse;
    }
}
