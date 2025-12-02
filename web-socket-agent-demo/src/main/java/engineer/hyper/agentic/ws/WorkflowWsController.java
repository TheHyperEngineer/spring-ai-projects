package engineer.hyper.agentic.ws;

import engineer.hyper.agentic.workflow.LangGraphWorkflowService;
import lombok.RequiredArgsConstructor;
import org.bsc.langgraph4j.GraphStateException;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class WorkflowWsController {

    private final LangGraphWorkflowService service;

    @MessageMapping("/workflow.start")
    public void start(@Payload Map<String, Object> payload, Principal principal) {
        String principalName = (principal != null && principal.getName() != null)
                ? principal.getName()
                : "anonymous";

        String workflowId = service.start(principalName);

        service.emitToUser(principalName, WorkflowEvent.builder()
                .type("workflow_started")
                .message("Workflow started")
                .workflowId(workflowId)
                .eventId(UUID.randomUUID().toString())
                .sequence(0L)
                .timestamp(Instant.now())
                .metadata(Map.of("ack", true))
                .build());
    }

    @MessageMapping("/workflow.userQuestion")
    public void handleQuestion(@Payload Map<String, Object> payload, Principal principal) throws GraphStateException {
        String principalName = (principal != null && principal.getName() != null)
                ? principal.getName()
                : "anonymous";

        String workflowId = (String) payload.get("workflowId");
        String question = (String) payload.get("question");
        service.handleUserQuestion(principalName, workflowId, question);
    }

    @MessageMapping("/workflow.userAnswer")
    public void handleAnswer(@Payload Map<String, Object> payload, Principal principal) throws GraphStateException {
        String principalName = (principal != null && principal.getName() != null)
                ? principal.getName()
                : "anonymous";

        String workflowId = (String) payload.get("workflowId");
        String promptId = (String) payload.get("promptId");
        String answer   = (String) payload.get("answer");
        service.handleUserAnswer(principalName, workflowId, promptId, answer);
    }
}