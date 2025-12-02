package engineer.hyper.agentic.agent;

import engineer.hyper.agentic.state.State;
import org.bsc.langgraph4j.action.NodeAction;

import java.util.Map;
import java.util.Scanner;

public class HumanApprovalNode implements NodeAction<State> {
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public Map<String, Object> apply(State state) {
        String input = state.getCurrentMessage();

        System.out.print("Type 'yes' to approve or 'no' to reject the connection suggestion: ");
        String approval = scanner.nextLine().trim().toLowerCase();
        return Map.of(
                State.CURRENT_MESSAGE_KEY, approval.equals("yes") ? input : "REJECTED",
                State.PREVIOUS_AGENT_KEY, "HUMAN_APPROVER"
        );
    }
}