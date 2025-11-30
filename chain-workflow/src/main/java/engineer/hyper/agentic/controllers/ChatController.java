package engineer.hyper.agentic.controllers;

import engineer.hyper.agentic.ChainWorkflow;
import engineer.hyper.agentic.services.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient chatClient;
    private final ChatService chatService;

    public ChatController(ChatClient.Builder builder, ChatService chatService) {
        this.chatService = chatService;
        this.chatClient = builder.build();
    }

    private final String REPORT = """
            Q3 Performance Summary:
            Our customer satisfaction score rose to 92 points this quarter.
            Revenue grew by 45% compared to last year.
            Market share is now at 23% in our primary market.
            Customer churn decreased to 5% from 8%.
            New user acquisition cost is $43 per user.
            Product adoption rate increased to 78%.
            Employee satisfaction is at 87 points.
            Operating margin improved to 34%.
            """;

    @GetMapping("/report")
    public String getReport() {
        log.info("Received request for performance report.");
        String chainResponse = new ChainWorkflow(chatClient).chain(REPORT);
        log.info("Sending performance report.");
        return chainResponse;
    }

    @PostMapping("/chat")
    public ResponseEntity<RequestOutput> chat(
            @RequestBody @Valid RequestInput input,
            @CookieValue(name = "X-CONV-ID", required = false) String convId) {
        log.info("Received chat request: {}", input.prompt());
        String conversationId = convId == null ? UUID.randomUUID().toString() : convId;

        String response = chatService.processUserQuery(input.prompt());
        log.info("Sending chat response: {}", response);

        ResponseCookie cookie = ResponseCookie.from("X-CONV-ID", conversationId)
                .path("/")
                .maxAge(3600)
                .build();
        RequestOutput output = new RequestOutput(response);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(output);
    }

    public record RequestInput(@NotBlank String prompt) { }

    public record RequestOutput(String content) { }
}
