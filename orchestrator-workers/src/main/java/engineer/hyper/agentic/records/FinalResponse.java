package engineer.hyper.agentic.records;

import java.util.List;

/**
 * Final response containing the orchestrator's analysis and combined worker
 * outputs.
 *
 * @param analysis        The orchestrator's understanding and breakdown of the
 *                        original task
 * @param workerResponses List of responses from workers, each handling a
 *                        specific subtask
 */
public record FinalResponse(String analysis, List<String> workerResponses) {
}