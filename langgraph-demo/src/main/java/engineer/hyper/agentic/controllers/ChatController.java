package engineer.hyper.agentic.controllers;

import engineer.hyper.agentic.graphs.GraphBuilder;
import engineer.hyper.agentic.state.State;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.NodeOutput;
import org.bsc.langgraph4j.StateGraph;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final GraphBuilder graphBuilder;

    @PostMapping
    ResponseEntity<Output> chat(@RequestBody @Valid Input input,
                                @CookieValue(name = "X-CONV-ID", required = false) String convId) throws GraphStateException {
        String conversationId = convId == null ? UUID.randomUUID().toString() : convId;

        // Build the state graph using the AgentExecutor
        StateGraph<State> graph = graphBuilder.build();
        var app = graph.compile();

        // Input data to initialize the workflow for full node execution
        Map<String, Object> inputData = Map.<String, Object>of(State.INPUT, Map.of("query", input.prompt()));

        String collected = app.stream(inputData)
                .stream()
                .map(NodeOutput::toString)
                .collect(Collectors.joining());

        /*var response = this.chatClient.prompt()
                .user(input.prompt())
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call().content();*/
        ResponseCookie cookie = ResponseCookie.from("X-CONV-ID", conversationId)
                .path("/")
                .maxAge(3600)
                .build();
        Output output = new Output(collected);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(output);
    }

    record Input(@NotBlank String prompt) {
    }

    record Output(String content) {
    }
}
