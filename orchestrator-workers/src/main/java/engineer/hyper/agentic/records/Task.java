package engineer.hyper.agentic.records;

/**
 * Represents a subtask identified by the orchestrator that needs to be executed
 * by a worker.
 *
 * @param type        The type or category of the task (e.g., "formal",
 *                    "conversational")
 * @param description Detailed description of what the worker should accomplish
 */
public record Task(String type, String description) {
}