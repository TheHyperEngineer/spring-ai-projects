package engineer.hyper.tool.controllers;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatClient chatClient;

    @PostMapping
    ResponseEntity<Output> chat(@RequestBody @Valid Input input) {
        var response = this.chatClient.prompt()
                .user(input.prompt())
                .call().content();
        Output output = new Output(response);
        return ResponseEntity.ok().body(output);
    }

    record Input(@NotBlank String prompt) {
    }

    record Output(String content) {
    }
}
