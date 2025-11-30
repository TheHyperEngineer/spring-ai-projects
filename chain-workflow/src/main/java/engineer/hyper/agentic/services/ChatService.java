package engineer.hyper.agentic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder builder, ChatMemory chatMemory) {
        this.chatClient
                = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        new SimpleLoggerAdvisor()
                ).build();
    }

    public String processUserQuery(String query) {
        log.info("Received user query: {}", query);
        // Placeholder for processing logic
        String topic = getTopic(query);
        if (topic != null && !topic.isEmpty()) {
            String news = getNewsHeadlines(topic);
            String tweets = getTweets(topic);
            return getTweets(topic, news, tweets);
        } else {
            return "Could not extract a valid topic from the query.";
        }
    }

    private String getTopic(String query) {
        String systemPrompt = """
                Your role is to review the user query and extract the topic of interest from it.
                Make sure the topic is relevant and extracted from user query.
                It should be a single topic to research on.
                """;
        SystemMessage systemMessage = new SystemMessage(systemPrompt);
        UserMessage userMessage = new UserMessage(query);

        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));

        return this.chatClient.prompt(prompt).call().content();
    }

    private String getNewsHeadlines(String topic) {
        String systemPrompt = """
                Your role is to provide a brief overview of the following:
                {topic}
                Make sure the overview is relevant to the topic and it should be a single short sentence.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        Map<String, Object> variables = Map.of("topic", topic);
        Message prompt = promptTemplate.createMessage(variables);
        return this.chatClient.prompt().messages(prompt).call().content();
    }

    private String getTweets(String topic) {
        String systemPrompt = """
                You are an expert content creator.
                Your job is to generate interesting tweet for the following:
                {topic}
                
                * Use a catchy first line to convey the essence of the topic.
                * Keep it concise and engaging.
                * Maintain a professional tone.
                * Use bullet points to list key features or benefits
                * Use emojis where appropriate.
                * Include relevant hashtags at the end.
                
                Don't use Markdown syntax. Use plain text format.
                """;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        Map<String, Object> variables = Map.of("topic", topic);
        Message prompt = promptTemplate.createMessage(variables);
        return this.chatClient.prompt().messages(prompt).call().content();
    }

    private String getTweets(String topic, String news, String tweets) {
        String systemPrompt = """
                You are an expert content creator.
                Your job is to compile {news} and {tweets} for the following: {topic}
                
                * Compile the report in a catchy and trendy format.
                * Keep it concise and engaging.
                * Maintain a professional tone.
                * Use bullet points to list key features or benefits
                * Use emojis where appropriate.
                * Include relevant hashtags at the end.
                
                """;
        PromptTemplate promptTemplate = new PromptTemplate(systemPrompt);
        Map<String, Object> variables = Map.of("topic", topic, "news", news, "tweets", tweets);
        Message prompt = promptTemplate.createMessage(variables);
        return this.chatClient.prompt().messages(prompt).call().content();
    }
}
