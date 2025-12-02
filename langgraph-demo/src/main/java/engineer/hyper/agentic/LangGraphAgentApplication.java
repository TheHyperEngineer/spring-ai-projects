package engineer.hyper.agentic;

import engineer.hyper.agentic.services.WeatherConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@Slf4j
@SpringBootApplication
@EnableConfigurationProperties(WeatherConfigProperties.class)
public class LangGraphAgentApplication {

 /*   private final AgentExecutor agentExecutor;
    private final GraphBuilder graphBuilder;

    public LangGraphAgentApplication(AgentExecutor agentExecutor, GraphBuilder graphBuilder) {
        this.graphBuilder = graphBuilder;
        this.agentExecutor = agentExecutor;
    }*/

    public static void main(String[] args) {
        SpringApplication.run(LangGraphAgentApplication.class, args);
    }

  /*  @Override
    public void run(String... args) throws Exception {
        log.info("Starting Multi-Agent AI Application");

        // Build the state graph using the AgentExecutor
        StateGraph<State> graph = graphBuilder.build();
        var app = graph.compile();

        // Input data to initialize the workflow
        var inputData = Map.<String, Object>of(
                State.INPUT, Map.of("query", "Current weather in Irving, Texas & Travel recommendations")
        );

        // Input data to initialize the workflow for full node execution
//        var inputData = Map.<String, Object>of(
//                AgentExecutor.State.INPUT, Map.of("query", "Current weather in Atlanta & Travel recommendations")
//        );


        // Execute the workflow and log the final output
        var result = app.invoke(inputData);
        log.info("Final Output: {}", result);
    }*/
}
