package engineer.hyper.agentic.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient codeGeneratorChatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel).defaultSystem("""
                You are a Java programmer tasked with generating high quality Java code.
                Your task is to Generate the best content possible for the user's request. If the user provides critique,
                respond with a revised version of your previous attempt.
                """).defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor()).build();
    }

    @Bean
    public ChatClient codeReviewerChatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel).defaultSystem("""
                You are tasked with generating critique and recommendations to the user's generated content.
                If the user content has something wrong or something to be improved, output a list of recommendations
                and critiques. If the user content is ok and there's nothing to change, output this: <OK>
                """).defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor()).build();
    }
}
