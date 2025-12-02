package engineer.hyper.agentic.graphs;

import engineer.hyper.agentic.agent.AgentRegistry;
import engineer.hyper.agentic.agent.Agents;
import engineer.hyper.agentic.agent.HumanApprovalNode;
import engineer.hyper.agentic.state.State;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.GraphDefinition.END;
import static org.bsc.langgraph4j.GraphDefinition.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
@Component
public class GraphBuilder {

    @Autowired
    private AgentRegistry agentRegistry;

    public StateGraph<State> build() throws GraphStateException {

        String opportunityAgent = Agents.OPPORTUNITY_AGENT.name();
        String upskillAgent = Agents.UPSKILL_AGENT.name();
        String connectionFinderAgent = Agents.CONNECTION_FINDER_AGENT.name();
        String connectionAgent = Agents.CONNECTION_AGENT.name();

        StateGraph<State> graph = new StateGraph<>(State.SCHEMA, State::new)
                .addNode(opportunityAgent, node_async(agentRegistry.get(opportunityAgent)))
                .addNode(upskillAgent, node_async(agentRegistry.get(upskillAgent)))
                .addNode(connectionFinderAgent, node_async(agentRegistry.get(connectionFinderAgent)))
                .addNode(connectionAgent, node_async(agentRegistry.get(connectionAgent)))
                .addNode("HUMAN_APPROVER", node_async(new HumanApprovalNode()));

        graph.addEdge(START, opportunityAgent);
        graph.addEdge(opportunityAgent, upskillAgent);
        graph.addEdge(upskillAgent, connectionFinderAgent);
        graph.addEdge(connectionFinderAgent, "HUMAN_APPROVER");
        graph.addConditionalEdges(
                "HUMAN_APPROVER",
                state -> CompletableFuture.completedFuture(state.getCurrentMessage().equals("REJECTED") ? connectionFinderAgent : connectionAgent),
                Map.of(connectionFinderAgent, connectionFinderAgent, connectionAgent, connectionAgent)
        );
        graph.addEdge(connectionAgent, END);

        return graph;
    }

}