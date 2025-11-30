package engineer.hyper.agentic.records;

import java.util.List;

/**
 * Response from the orchestrator containing task analysis and breakdown into
 * subtasks.
 *
 * @param analysis Detailed explanation of the task and how different approaches
 *                 serve its aspects
 * @param tasks    List of subtasks identified by the orchestrator to be
 *                 executed by workers
 */
public record OrchestratorResponse(String analysis, List<Task> tasks) {
}