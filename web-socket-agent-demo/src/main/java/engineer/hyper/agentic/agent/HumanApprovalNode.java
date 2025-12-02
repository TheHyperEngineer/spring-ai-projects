// src/main/java/engineer/hyper/agentic/agent/HumanApprovalNode.java
package engineer.hyper.agentic.agent;

import engineer.hyper.agentic.state.State;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;

/**
 * Non-blocking marker node. The service layer will detect this
 * and emit a human_input_required event, pausing execution.
 */
public class HumanApprovalNode implements NodeAction<State> {

    public static final String AWAITING_HUMAN = "__AWAITING_HUMAN__";

    @Override
    public Map<String, Object> apply(State state) {
        return Map.of(
            State.CURRENT_MESSAGE_KEY, AWAITING_HUMAN,
            State.PREVIOUS_AGENT_KEY, "HUMAN_APPROVER"
        );
    }
}