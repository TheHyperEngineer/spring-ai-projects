package engineer.hyper.agentic.records;

import java.util.List;

/**
 * Represents the final refined response. Contains the final solution and the
 * chain of thought showing the evolution of the solution.
 *
 * @param solution       The final solution
 * @param chainOfThought The chain of thought showing the evolution of the
 *                       solution
 */
public record RefinedResponse(String solution, List<Generation> chainOfThought) {
}