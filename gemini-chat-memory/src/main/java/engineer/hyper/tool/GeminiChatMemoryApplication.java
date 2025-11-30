package engineer.hyper.tool;

import org.springframework.ai.model.openai.autoconfigure.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(
        exclude = {
                OpenAiChatAutoConfiguration.class,
                OpenAiAudioSpeechAutoConfiguration.class,
                OpenAiAudioTranscriptionAutoConfiguration.class,
                OpenAiEmbeddingAutoConfiguration.class,
                OpenAiImageAutoConfiguration.class,
                OpenAiModerationAutoConfiguration.class
        }
)
public class GeminiChatMemoryApplication {
    public static void main(String[] args) {
        SpringApplication.run(GeminiChatMemoryApplication.class);
    }
}
