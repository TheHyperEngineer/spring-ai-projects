package engineer.hyper.agentic.controllers;

import engineer.hyper.agentic.records.Generation;
import engineer.hyper.agentic.records.RefinedResponse;
import engineer.hyper.agentic.services.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/chat")
    public ResponseEntity<RequestOutput> chat(
            @RequestBody @Valid RequestInput input,
            @CookieValue(name = "X-CONV-ID", required = false) String convId) {
        log.info("Received chat request: {}", input.prompt());
        String conversationId = convId == null ? UUID.randomUUID().toString() : convId;

        RefinedResponse refinedResponse = chatService.processUserQuery(input.prompt());
        log.info("Sending chat response: {}", refinedResponse);

        ResponseCookie cookie = ResponseCookie.from("X-CONV-ID", conversationId)
                .path("/")
                .maxAge(3600)
                .build();
        RequestOutput output = new RequestOutput(refinedResponse.solution(), refinedResponse.chainOfThought());
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(output);
    }

    public record RequestInput(@NotBlank String prompt) { }

    public record RequestOutput(String content, List<Generation> chainOfThought) { }
}
