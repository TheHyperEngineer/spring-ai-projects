package engineer.hyper.agentic.services;

import engineer.hyper.agentic.agents.ParallelizationWorkflow;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ParallelizationWorkflow parallelizationWorkflow;
    private final String SYSTEM_PROMPT = """
            Analyze how market changes will impact this stakeholder group.
            Provide specific impacts and recommended actions.
            Format with clear sections and priorities.
            """;

    private final List<String> inputs = List.of(
            """
                    Customers:
                    - Price sensitive
                    - Want better tech
                    - Environmental concerns
                    """,

            """
                    Employees:
                    - Job security worries
                    - Need new skills
                    - Want clear direction
                    """,

            """
                    Investors:
                    - Expect growth
                    - Want cost control
                    - Risk concerns
                    """,

            """
                    Suppliers:
                    - Capacity constraints
                    - Price pressures
                    - Tech transitions
                    """);

    private final int nWorkers = 4;

    public List<String> processUserQuery(String input) {
        log.info("Received user query: {}", input);
        // Placeholder for processing logic
        return parallelizationWorkflow.parallel(SYSTEM_PROMPT, inputs, nWorkers);
    }
}
