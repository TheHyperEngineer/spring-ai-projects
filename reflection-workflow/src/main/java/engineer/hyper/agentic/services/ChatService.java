package engineer.hyper.agentic.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatClient codeGeneratorChatClient;
    private final ChatClient codeReviewerChatClient;

    private static final int MAX_ITERATIONS = 5;

    public String processUserQuery(String userQuestion) {
        String generation = codeGeneratorChatClient.prompt(userQuestion).call().content();
        log.info("##Generation\n\n{}", generation);

        if (generation == null || generation.isBlank()) {
            log.warn("Generation is null or blank, returning early.");
            return generation;
        }

        String critique;
        for (int i = 0; i < MAX_ITERATIONS; i++) {
            critique = codeReviewerChatClient.prompt(generation).call().content();
            log.info("##Critique\n\n{}", critique);
            if (critique != null && !critique.isBlank() && critique.contains("<OK>")) {
                log.info("\n\nStop sequence found\n\n");
                break;
            }
            if (critique != null && !critique.isBlank()) {
                generation = codeGeneratorChatClient.prompt(critique).call().content();
            }
        }
        return generation;
    }
}
