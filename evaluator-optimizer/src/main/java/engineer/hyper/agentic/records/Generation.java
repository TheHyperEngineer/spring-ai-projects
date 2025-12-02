package engineer.hyper.agentic.records;

/**
 * Represents a solution generation step. Contains the model's thoughts and the
 * proposed solution.
 *
 * @param thoughts The model's understanding of the task and feedback
 * @param response The model's proposed solution
 */
public record Generation(String thoughts, String response) {
}