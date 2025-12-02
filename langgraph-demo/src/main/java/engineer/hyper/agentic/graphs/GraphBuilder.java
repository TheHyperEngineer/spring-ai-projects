package engineer.hyper.agentic.graphs;

import engineer.hyper.agentic.executors.AgentExecutor;
import engineer.hyper.agentic.state.State;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.bsc.langgraph4j.GraphDefinition.END;
import static org.bsc.langgraph4j.GraphDefinition.START;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

/**
 * Builder class to construct a StateGraph for the agent workflow.
 */
@Slf4j
@Component
public class GraphBuilder {
    private final AgentExecutor agentExecutor;

    public GraphBuilder(AgentExecutor agentExecutor) {
        this.agentExecutor = agentExecutor;
    }

    /**
     * Builds the workflow graph by defining nodes and transitions.
     *
     * @return The constructed StateGraph.
     * @throws GraphStateException If the graph cannot be constructed.
     */
    public StateGraph<State> build() throws GraphStateException {

        StateGraph<State> graph = new StateGraph<>(State.SCHEMA, State::new)
                .addNode("weatherAgent", node_async(agentExecutor::callWeatherAgent))
                .addNode("travelAgent", node_async(agentExecutor::callTravelAgent))
                .addNode("foodAgent", node_async(agentExecutor::callFoodAgent));

        graph.addEdge(START, "weatherAgent");
        graph.addConditionalEdges(
                "weatherAgent",
                state -> CompletableFuture.completedFuture(
                        state.getCallTravelAgent() ? "travelAgent" : "end"),
                Map.of("travelAgent", "travelAgent", "end", END)
        );
        graph.addEdge("travelAgent", "foodAgent");
        graph.addEdge("foodAgent", END);

        return graph;
    }
}