package engineer.hyper.agentic.agents;

import engineer.hyper.agentic.records.EvaluationResponse;
import engineer.hyper.agentic.records.Generation;
import engineer.hyper.agentic.records.RefinedResponse;
import engineer.hyper.agentic.services.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EvaluatorOptimizer {

    private final ChatClient chatClient;
    private final String generatorPrompt = ChatService.DEFAULT_GENERATOR_PROMPT;
    private final String evaluatorPrompt = ChatService.DEFAULT_EVALUATOR_PROMPT;


    /**
     * Initiates the evaluator-optimizer workflow for a given task. This method
     * orchestrates the iterative process of generation and evaluation until a
     * satisfactory solution is reached.
     *
     * <p>
     * The workflow follows these steps:
     * </p>
     * <ol>
     * <li>Generate an initial solution</li>
     * <li>Evaluate the solution against quality criteria</li>
     * <li>If evaluation passes, return the solution</li>
     * <li>If evaluation indicates need for improvement, incorporate feedback and
     * generate new solution</li>
     * <li>Repeat steps 2-4 until a satisfactory solution is achieved</li>
     * </ol>
     *
     * @param task The task or problem to be solved through iterative refinement
     * @return A RefinedResponse containing the final solution and the chain of
     * thought
     * showing the evolution of the solution
     */
    public RefinedResponse loop(String task) {
        List<String> memory = new ArrayList<>();
        List<Generation> chainOfThought = new ArrayList<>();

        return loop(task, "", memory, chainOfThought);
    }

    /**
     * Internal recursive implementation of the evaluator-optimizer loop. This
     * method
     * maintains the state of previous attempts and feedback while recursively
     * refining
     * the solution until it meets the evaluation criteria.
     *
     * @param task           The original task to be solved
     * @param context        Accumulated context including previous attempts and
     *                       feedback
     * @param memory         List of previous solution attempts for reference
     * @param chainOfThought List tracking the evolution of solutions and reasoning
     * @return A RefinedResponse containing the final solution and complete solution
     * history
     */
    private RefinedResponse loop(String task, String context, List<String> memory, List<Generation> chainOfThought) {

        Generation generation = generate(task, context);
        memory.add(generation.response());
        chainOfThought.add(generation);

        EvaluationResponse evaluationResponse = evaluate(generation.response(), task);

        if (evaluationResponse.evaluation().equals(EvaluationResponse.Evaluation.PASS)) {
            // Solution is accepted!
            return new RefinedResponse(generation.response(), chainOfThought);
        }

        // Accumulated new context including the last and the previous attempts and
        // feedbacks.
        StringBuilder newContext = new StringBuilder();
        newContext.append("Previous attempts:");
        for (String m : memory) {
            newContext.append("\n- ").append(m);
        }
        newContext.append("\nFeedback: ").append(evaluationResponse.feedback());

        return loop(task, newContext.toString(), memory, chainOfThought);
    }

    /**
     * Generates or refines a solution based on the given task and feedback context.
     * This method represents the generator component of the workflow, producing
     * responses that can be iteratively improved through evaluation feedback.
     *
     * @param task    The primary task or problem to be solved
     * @param context Previous attempts and feedback for iterative improvement
     * @return A Generation containing the model's thoughts and proposed solution
     */
    private Generation generate(String task, String context) {
        Generation generationResponse = chatClient.prompt()
                .user(u -> u.text("{prompt}\n{context}\nTask: {task}")
                        .param("prompt", this.generatorPrompt)
                        .param("context", context)
                        .param("task", task))
                .call()
                .entity(Generation.class);

        log.info("\n=== GENERATOR OUTPUT ===\nTHOUGHTS: {}\n\nRESPONSE:\n {}\n", generationResponse.thoughts(), generationResponse.response());
        return generationResponse;
    }

    /**
     * Evaluates if a solution meets the specified requirements and quality
     * criteria.
     * This method represents the evaluator component of the workflow, analyzing
     * solutions
     * and providing detailed feedback for further refinement until the desired
     * quality
     * level is reached.
     *
     * @param content The solution content to be evaluated
     * @param task    The original task against which to evaluate the solution
     * @return An EvaluationResponse containing the evaluation result
     * (PASS/NEEDS_IMPROVEMENT/FAIL)
     * and detailed feedback for improvement
     */
    private EvaluationResponse evaluate(String content, String task) {

        EvaluationResponse evaluationResponse = chatClient.prompt()
                .user(u -> u.text("{prompt}\nOriginal task: {task}\nContent to evaluate: {content}")
                        .param("prompt", this.evaluatorPrompt)
                        .param("task", task)
                        .param("content", content))
                .call()
                .entity(EvaluationResponse.class);

        log.info("\n=== EVALUATOR OUTPUT ===\nEVALUATION: {}\n\nFEEDBACK: {}\n", evaluationResponse.evaluation(), evaluationResponse.feedback());
        return evaluationResponse;
    }

}