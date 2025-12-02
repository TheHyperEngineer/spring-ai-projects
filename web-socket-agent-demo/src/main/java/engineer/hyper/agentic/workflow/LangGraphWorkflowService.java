package engineer.hyper.agentic.workflow;

import engineer.hyper.agentic.agent.HumanApprovalNode;
import engineer.hyper.agentic.graphs.GraphBuilder;
import engineer.hyper.agentic.state.State;
import engineer.hyper.agentic.ws.ChatMessage;
import engineer.hyper.agentic.ws.WorkflowEvent;
import lombok.RequiredArgsConstructor;
import org.bsc.async.AsyncGenerator;
import org.bsc.langgraph4j.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LangGraphWorkflowService {

    private final GraphBuilder graphBuilder;
    private final WorkflowSessionStore store;
    private final SimpMessagingTemplate messagingTemplate;

    // Build once; reuse compiled graph
    private volatile CompiledGraph<State> compiledGraph;

    private CompiledGraph<State> graph() throws GraphStateException {
        if (compiledGraph == null) {
            synchronized (this) {
                if (compiledGraph == null) {
                    try {
                        StateGraph<State> sg = graphBuilder.build();
                        compiledGraph = sg.compile();
                    } catch (Exception e) {
                        throw new IllegalStateException("Failed to compile graph", e);
                    }
                }
            }
        }
        return compiledGraph;
    }

    // Start a session and emit an initial greeting. The controller will emit workflow_started.
    public String start(String userPrincipalName) {
        String workflowId = store.newSessionId();
        WorkflowSessionStore.Session session = store.require(workflowId);

        session.setStage(WorkflowSessionStore.Stage.GREETING_SENT);
        session.setStateSnapshot(null);
        // If supported: store.resetSeq(workflowId);

        emitToUser(userPrincipalName, WorkflowEvent.builder()
                .type("system_message")
                .message("Hello! What are you here for?")
                .workflowId(workflowId)
                .eventId(UUID.randomUUID().toString())
                .sequence(store.nextSeq(workflowId))
                .timestamp(Instant.now())
                .metadata(Map.of("stage", session.getStage().name()))
                .build());

        return workflowId;
    }

    // Handle a free-form user question and stream graph snapshots.
    public void handleUserQuestion(String userPrincipalName, String workflowId, String question) throws GraphStateException {
        if (workflowId == null || workflowId.isBlank()) {
            emitError(userPrincipalName, "UNKNOWN", "Missing workflowId in user question",
                    Map.of("reason", "workflowId_null_or_blank"));
            return;
        }

        WorkflowSessionStore.Session session = store.require(workflowId);
        session.getHistory().add(new ChatMessage("user", question));
        session.setStage(WorkflowSessionStore.Stage.RUNNING);

        emitThinking(userPrincipalName, workflowId, "Processing your request...");

        Map<String, Object> init = Map.of(
                State.CURRENT_MESSAGE_KEY, question,
                State.PREVIOUS_AGENT_KEY, "user",
                State.PREVIOUS_MESSAGES_KEY, normalizeHistory(session.getHistory())
        );

        RunnableConfig config = RunnableConfig.builder().build();
        AsyncGenerator.Cancellable<NodeOutput<State>> stream = graph().streamSnapshots(init, config);

        try {
            for (NodeOutput<State> nodeOut : stream.stream().toList()) {
                State state = nodeOut.state();
                Map<String, Object> stateOut = state.data();

                // Persist latest snapshot for resume
                session.setStateSnapshot(stateOut);

                String currentMessage = (String) stateOut.getOrDefault(State.CURRENT_MESSAGE_KEY, "");
                String previousAgent = (String) stateOut.getOrDefault(State.PREVIOUS_AGENT_KEY, "UNKNOWN");
                String nodeId = nodeOut.node();

                // Emit meaningful progress
                if (currentMessage != null && !currentMessage.isBlank()
                        && !HumanApprovalNode.AWAITING_HUMAN.equals(currentMessage)) {
                    emitToUser(userPrincipalName, WorkflowEvent.builder()
                            .type("workflow_progress")
                            .message(currentMessage)
                            .workflowId(workflowId)
                            .eventId(UUID.randomUUID().toString())
                            .sequence(store.nextSeq(workflowId))
                            .timestamp(Instant.now())
                            .metadata(Map.of(
                                    "agent", previousAgent,
                                    "nodeId", nodeId
                            ))
                            .build());
                }

                // Pause for human approval
                if (HumanApprovalNode.AWAITING_HUMAN.equals(currentMessage)) {
                    WorkflowSessionStore.Prompt prompt = new WorkflowSessionStore.Prompt();
                    prompt.setPromptId(UUID.randomUUID().toString());
                    prompt.setQuestion("Approve sending a connection request? Yes/No");
                    prompt.setChoices(List.of("Yes", "No"));
                    prompt.setNextOnApproveNode("CONNECTION_AGENT");
                    prompt.setNextOnRejectNode("CONNECTION_FINDER_AGENT");

                    session.setPendingPrompt(prompt);
                    session.setStage(WorkflowSessionStore.Stage.WAITING_FOR_CONFIRMATION);

                    emitToUser(userPrincipalName, WorkflowEvent.builder()
                            .type("human_input_required")
                            .message(prompt.getQuestion())
                            .workflowId(workflowId)
                            .eventId(UUID.randomUUID().toString())
                            .sequence(store.nextSeq(workflowId))
                            .timestamp(Instant.now())
                            .metadata(Map.of(
                                    "promptId", prompt.getPromptId(),
                                    "choices", prompt.getChoices(),
                                    "agent", previousAgent,
                                    "nodeId", nodeId
                            ))
                            .build());

                    stream.cancel(true);
                    return;
                }
            }

            // Finished without human pause
            Map<String, Object> finalState = session.getStateSnapshot() != null
                    ? session.getStateSnapshot()
                    : Map.of();

            String finalMessage = (String) finalState.getOrDefault(State.CURRENT_MESSAGE_KEY, "");
            String finalAgent = (String) finalState.getOrDefault(State.PREVIOUS_AGENT_KEY, "UNKNOWN");

            if (finalMessage != null && !finalMessage.isBlank()) {
                session.getHistory().add(new ChatMessage(finalAgent, finalMessage));
            }
            session.setStage(WorkflowSessionStore.Stage.COMPLETED);

            emitToUser(userPrincipalName, WorkflowEvent.builder()
                    .type("workflow_completed")
                    .message(finalMessage)
                    .workflowId(workflowId)
                    .eventId(UUID.randomUUID().toString())
                    .sequence(store.nextSeq(workflowId))
                    .timestamp(Instant.now())
                    .metadata(Map.of("agent", finalAgent))
                    .build());

        } catch (Exception e) {
            session.setStage(WorkflowSessionStore.Stage.ERROR);
            emitError(userPrincipalName, workflowId,
                    "Graph execution failed: " + e.getMessage(),
                    Map.of("exception", e.getClass().getSimpleName()));
        }
    }

    // Resume after human input and continue streaming along selected branch.
    public void handleUserAnswer(String userPrincipalName, String workflowId, String promptId, String answerRaw) throws GraphStateException {
        WorkflowSessionStore.Session session = store.require(workflowId);
        WorkflowSessionStore.Prompt prompt = session.getPendingPrompt();

        if (prompt == null || !prompt.getPromptId().equals(promptId)) {
            emitError(userPrincipalName, workflowId, "Invalid or expired prompt", Map.of("promptId", promptId));
            return;
        }

        String answer = answerRaw == null ? "" : answerRaw.trim().toLowerCase(Locale.ROOT);
        boolean approved = answer.startsWith("y");
        String branch = approved ? prompt.getNextOnApproveNode() : prompt.getNextOnRejectNode();
        String resumedMessage = approved ? "APPROVED" : "REJECTED";

        Map<String, Object> resumedState = new HashMap<>(session.getStateSnapshot() != null
                ? session.getStateSnapshot()
                : Map.of());
        resumedState.put(State.CURRENT_MESSAGE_KEY, resumedMessage);
        resumedState.put(State.PREVIOUS_AGENT_KEY, "HUMAN_APPROVER");
        // Optionally include branch hint in state if your graph uses it
        resumedState.put("BRANCH_HINT", branch);

        session.setPendingPrompt(null);
        session.setStage(WorkflowSessionStore.Stage.RUNNING);
        emitThinking(userPrincipalName, workflowId, "Resuming workflow...");

        RunnableConfig config = RunnableConfig.builder().build();
        AsyncGenerator.Cancellable<NodeOutput<State>> stream = graph().streamSnapshots(resumedState, config);

        try {
            for (NodeOutput<State> nodeOut : stream.stream().toList()) {
                State state = nodeOut.state();
                Map<String, Object> stateOut = state.data();

                session.setStateSnapshot(stateOut);

                String currentMessage = (String) stateOut.getOrDefault(State.CURRENT_MESSAGE_KEY, "");
                String previousAgent = (String) stateOut.getOrDefault(State.PREVIOUS_AGENT_KEY, "UNKNOWN");
                String nodeId = nodeOut.node();

                if (currentMessage != null && !currentMessage.isBlank()
                        && !HumanApprovalNode.AWAITING_HUMAN.equals(currentMessage)) {
                    emitToUser(userPrincipalName, WorkflowEvent.builder()
                            .type("workflow_progress")
                            .message(currentMessage)
                            .workflowId(workflowId)
                            .eventId(UUID.randomUUID().toString())
                            .sequence(store.nextSeq(workflowId))
                            .timestamp(Instant.now())
                            .metadata(Map.of(
                                    "agent", previousAgent,
                                    "nodeId", nodeId,
                                    "branch", branch
                            ))
                            .build());
                }

                if (HumanApprovalNode.AWAITING_HUMAN.equals(currentMessage)) {
                    WorkflowSessionStore.Prompt nextPrompt = new WorkflowSessionStore.Prompt();
                    nextPrompt.setPromptId(UUID.randomUUID().toString());
                    nextPrompt.setQuestion("Approve sending a connection request? Yes/No");
                    nextPrompt.setChoices(List.of("Yes", "No"));
                    nextPrompt.setNextOnApproveNode("CONNECTION_AGENT");
                    nextPrompt.setNextOnRejectNode("CONNECTION_FINDER_AGENT");

                    session.setPendingPrompt(nextPrompt);
                    session.setStage(WorkflowSessionStore.Stage.WAITING_FOR_CONFIRMATION);

                    emitToUser(userPrincipalName, WorkflowEvent.builder()
                            .type("human_input_required")
                            .message(nextPrompt.getQuestion())
                            .workflowId(workflowId)
                            .eventId(UUID.randomUUID().toString())
                            .sequence(store.nextSeq(workflowId))
                            .timestamp(Instant.now())
                            .metadata(Map.of(
                                    "promptId", nextPrompt.getPromptId(),
                                    "choices", nextPrompt.getChoices(),
                                    "agent", previousAgent,
                                    "nodeId", nodeId,
                                    "branch", branch
                            ))
                            .build());

                    stream.cancel(true);
                    return;
                }
            }

            Map<String, Object> finalState = session.getStateSnapshot() != null
                    ? session.getStateSnapshot()
                    : Map.of();

            String finalMessage = (String) finalState.getOrDefault(State.CURRENT_MESSAGE_KEY, "");
            String finalAgent = (String) finalState.getOrDefault(State.PREVIOUS_AGENT_KEY, "UNKNOWN");

            if (finalMessage != null && !finalMessage.isBlank()) {
                session.getHistory().add(new ChatMessage(finalAgent, finalMessage));
            }
            session.setStage(WorkflowSessionStore.Stage.COMPLETED);

            emitToUser(userPrincipalName, WorkflowEvent.builder()
                    .type("workflow_completed")
                    .message(finalMessage)
                    .workflowId(workflowId)
                    .eventId(UUID.randomUUID().toString())
                    .sequence(store.nextSeq(workflowId))
                    .timestamp(Instant.now())
                    .metadata(Map.of(
                            "agent", finalAgent,
                            "branch", branch
                    ))
                    .build());

        } catch (Exception e) {
            session.setStage(WorkflowSessionStore.Stage.ERROR);
            emitError(userPrincipalName, workflowId,
                    "Graph resume failed: " + e.getMessage(),
                    Map.of("exception", e.getClass().getSimpleName()));
        }
    }

    // Normalize chat history into {role, content} pairs
    private List<Map<String, String>> normalizeHistory(List<ChatMessage> history) {
        List<Map<String, String>> msgs = new ArrayList<>();
        for (ChatMessage m : history) {
            msgs.add(Map.of("role", m.getRole(), "content", m.getContent()));
        }
        return msgs;
    }

    // Emit a consistent "thinking" signal
    private void emitThinking(String user, String workflowId, String message) {
        emitToUser(user, WorkflowEvent.builder()
                .type("agent_thinking")
                .message(message)
                .workflowId(workflowId)
                .eventId(UUID.randomUUID().toString())
                .sequence(store.nextSeq(workflowId))
                .timestamp(Instant.now())
                .metadata(Map.of("spinner", true))
                .build());
    }

    // Emit structured error events
    private void emitError(String user, String workflowId, String msg, Map<String, Object> meta) {
        emitToUser(user, WorkflowEvent.builder()
                .type("workflow_error")
                .message(msg)
                .workflowId(workflowId)
                .eventId(UUID.randomUUID().toString())
                .sequence(store.nextSeq(workflowId))
                .timestamp(Instant.now())
                .metadata(meta)
                .build());
    }

    // Targeted delivery: /user/{principal}/queue/chat
    public void emitToUser(String userPrincipalName, WorkflowEvent event) {
        messagingTemplate.convertAndSendToUser(userPrincipalName, "/queue/chat", event);
    }
}