package engineer.hyper.agentic.agents;

import engineer.hyper.agentic.records.FinalResponse;
import engineer.hyper.agentic.records.OrchestratorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrchestratorWorkers {

    public static final String DEFAULT_ORCHESTRATOR_PROMPT = """
            Analyze this task and break it down into 2-3 distinct approaches:
            
            Task: {task}
            
            Return your response in this JSON format:
            \\{
            "analysis": "Explain your understanding of the task and which variations would be valuable.
                         Focus on how each approach serves different aspects of the task.",
            "tasks": [
            	\\{
            	"type": "formal",
            	"description": "Write a precise, technical version that emphasizes specifications"
            	\\},
            	\\{
            	"type": "conversational",
            	"description": "Write an engaging, friendly version that connects with readers"
            	\\}
            ]
            \\}
            """;

    public static final String DEFAULT_WORKER_PROMPT = """
            Generate content based on:
            Task: {original_task}
            Style: {task_type}
            Guidelines: {task_description}
            """;

    /**
     * Processes a task using the orchestrator-workers pattern.
     * First, the orchestrator analyzes the task and breaks it down into subtasks.
     * Then, workers execute each subtask in parallel.
     * Finally, the results are combined into a single response.
     *
     * @param taskDescription Description of the task to be processed
     * @return WorkerResponse containing the orchestrator's analysis and combined
     * worker outputs
     * @throws IllegalArgumentException if taskDescription is null or empty
     */


    private final ChatClient chatClient;
    private final String orchestratorPrompt = DEFAULT_ORCHESTRATOR_PROMPT;
    private final String workerPrompt = DEFAULT_WORKER_PROMPT;


    public FinalResponse process(String taskDescription) {
        Assert.hasText(taskDescription, "Task description must not be empty");

        // Step 1: Get orchestrator response
        OrchestratorResponse orchestratorResponse = this.chatClient.prompt()
                .user(u -> u.text(this.orchestratorPrompt)
                        .param("task", taskDescription))
                .call()
                .entity(OrchestratorResponse.class);

        System.out.println(String.format("\n=== ORCHESTRATOR OUTPUT ===\nANALYSIS: %s\n\nTASKS: %s\n",
                orchestratorResponse.analysis(), orchestratorResponse.tasks()));

        // Step 2: Process each task
        List<String> workerResponses = orchestratorResponse.tasks().stream().map(task -> this.chatClient.prompt()
                .user(u -> u.text(this.workerPrompt)
                        .param("original_task", taskDescription)
                        .param("task_type", task.type())
                        .param("task_description", task.description()))
                .call()
                .content()).toList();

        System.out.println("\n=== WORKER OUTPUT ===\n" + workerResponses);

        return new FinalResponse(orchestratorResponse.analysis(), workerResponses);
    }

}