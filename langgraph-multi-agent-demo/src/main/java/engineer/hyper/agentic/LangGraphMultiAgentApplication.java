package engineer.hyper.agentic;

import engineer.hyper.agentic.graphs.GraphBuilder;
import engineer.hyper.agentic.state.State;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class LangGraphMultiAgentApplication implements CommandLineRunner {

    @Autowired
    private GraphBuilder graphBuilder;

    public static void main(String[] args) {
        SpringApplication.run(LangGraphMultiAgentApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        StateGraph<State> stateGraph = graphBuilder.build();
        CompiledGraph<State> compiledGraph = stateGraph.compile();

        List<Map<String, String>> messages = new ArrayList<>();
        String humanInputMessage = "Get the best job opportunities for XY, he is a software engineer with 5 years of experience in New York";
        messages.add(Map.of("HUMAN INPUT", humanInputMessage));

        compiledGraph.invoke(Map.of(
                State.CURRENT_MESSAGE_KEY, humanInputMessage,
                State.PREVIOUS_AGENT_KEY, "HUMAN INPUT",
                State.PREVIOUS_MESSAGES_KEY, messages
        ));
    }
}
