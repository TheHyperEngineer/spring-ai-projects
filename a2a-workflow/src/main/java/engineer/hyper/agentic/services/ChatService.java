package engineer.hyper.agentic.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    private final OllamaChatModel ollamaChatModel;
    private final OpenAiChatModel openAiChatModel;

    private static final int MAX_ITERATIONS = 5;

    public String processUserQuery(String userQuestion) {
        SelfRefineEvaluationAdvisor selfRefineEvaluationAdvisor
                = SelfRefineEvaluationAdvisor.builder()
                .chatClientBuilder(ChatClient.builder(ollamaChatModel))
                .maxRepeatAttempts(15)
                .successRating(4)
                .order(0)
                .build();

        ChatClient chatClient
                = ChatClient
                .builder(openAiChatModel)
                .defaultTools(new MyTools())
                .defaultAdvisors(selfRefineEvaluationAdvisor, new MyLoggingAdvisor(2))
                .build();

        String generation = chatClient.prompt(userQuestion).call().content();
        log.info("##Generation\n\n{}", generation);

        return generation;
    }

}
