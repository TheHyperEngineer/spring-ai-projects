package engineer.hyper.ollama.chat.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @PostMapping
    @CrossOrigin
    Output chat(@RequestBody @Valid Input input) {
        log.info("Received chat request: {}", input);
        String response = chatClient.prompt(input.prompt()).call().content();
        log.info("Sending chat response: {}", response);
        return new Output(response);
    }

    record Input(@NotBlank String prompt) {
    }

    record Output(String content) {
    }
}
