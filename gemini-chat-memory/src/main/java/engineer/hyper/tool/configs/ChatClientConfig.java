package engineer.hyper.tool.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.ai.chat.client.ChatClient.builder;

@Configuration
public class ChatClientConfig {

    @Value("${app.api-key}")
    private String apiKey;

    @Bean
    public ChatClient chatClient(ChatMemory chatMemory) {
        return builder(openAiChatModel())
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .build();
    }

    @Bean
    public OpenAiChatModel openAiChatModel() {
        return OpenAiChatModel.builder()
                .openAiApi(
                        OpenAiApi.builder()
                                .baseUrl("https://generativelanguage.googleapis.com")
                                .completionsPath("/v1beta/openai/chat/completions")
                                .apiKey(apiKey)
                                .build()
                )
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .model("gemini-2.0-flash")
                                .temperature(0.6)
                                .build()
                )
                .build();
    }
}
