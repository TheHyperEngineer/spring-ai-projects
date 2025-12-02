// src/main/java/engineer/hyper/agentic/ws/WorkflowEvent.java
package engineer.hyper.agentic.ws;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class WorkflowEvent {
    private String type;       // system_message, agent_thinking, human_input_required, workflow_progress, workflow_error, workflow_completed
    private String message;    // human-readable message
    private String workflowId; // correlation id per session
    private String eventId;    // unique id per event (UUID)
    private long sequence;     // monotonic per workflowId
    private Instant timestamp; // ISO-8601
    private Map<String, Object> metadata; // promptId, choices, chunkId, agentName, etc.
}