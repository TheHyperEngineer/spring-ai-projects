// src/main/java/engineer/hyper/agentic/workflow/WorkflowSessionStore.java
package engineer.hyper.agentic.workflow;

import engineer.hyper.agentic.ws.ChatMessage;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class WorkflowSessionStore {

    public enum Stage { INIT, GREETING_SENT, RUNNING, WAITING_FOR_CONFIRMATION, COMPLETED, CANCELLED, ERROR }

    @Data
    public static class Prompt {
        private String promptId;
        private String question;
        private List<String> choices; // e.g., ["Yes", "No"]
        private Instant createdAt = Instant.now();
        private String nextOnApproveNode; // branch node id
        private String nextOnRejectNode;  // branch node id
    }

    @Data
    public static class Session {
        private String workflowId;
        private Stage stage = Stage.INIT;
        private List<ChatMessage> history = new ArrayList<>();
        private AtomicLong sequence = new AtomicLong(0);
        private Prompt pendingPrompt;
        private Map<String, Object> stateSnapshot = new HashMap<>(); // last graph state map
    }

    private final Map<String, Session> sessions = new ConcurrentHashMap<>();

    public String newSessionId() {
        String id = UUID.randomUUID().toString();
        Session s = new Session();
        s.setWorkflowId(id);
        sessions.put(id, s);
        return id;
    }

    public Optional<Session> get(String id) {
        return Optional.ofNullable(sessions.get(id));
    }

    public Session require(String id) {
        return get(id).orElseThrow(() -> new IllegalArgumentException("Unknown workflowId: " + id));
    }

    public void complete(String id) {
        require(id).setStage(Stage.COMPLETED);
    }

    public long nextSeq(String id) {
        return require(id).getSequence().incrementAndGet();
    }
}