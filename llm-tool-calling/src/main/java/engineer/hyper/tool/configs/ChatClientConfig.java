package engineer.hyper.tool.configs;

import engineer.hyper.tool.tools.DateTimeTools;
import engineer.hyper.tool.tools.EmployeeTools;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ChatClientConfig {

    private final EmployeeTools employeeTools;
    private final DateTimeTools dateTimeTools;

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultSystem("""
                        You are a helpful assistant for Hyper Engineer company.
                        You always respond based on the data you have from tools available to you.
                        If you don't know the answer, you will respond with "I don't know".
                        """)
                .defaultTools(employeeTools, dateTimeTools)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor())
                .build();
    }
}
