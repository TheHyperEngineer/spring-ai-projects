package engineer.hyper.agentic.configs;

import engineer.hyper.agentic.tools.ConnectionAgentTools;
import engineer.hyper.agentic.tools.ConnectionFinderTools;
import engineer.hyper.agentic.tools.JobsAndOpportunityTools;
import engineer.hyper.agentic.tools.UpSkillTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient opportunityChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are an **Opportunity Discovery Agent**. 
                        Your role is to act as a career advisor who identifies job and project opportunities that match a user’s existing skills and interests. 
                        - Always gather relevant opportunities using the available tools. 
                        - Find a single opportunity that best aligns with the user’s profile and goals.
                        - Return only one opportunity suggestion, along with a brief explanation of why it is a good fit.
                        - DO NOT TALK ABOUT NEXT STEPS OR OTHER SUGGESTIONS, your role is only to find the best opportunity match.
                        """)
                .defaultTools(new JobsAndOpportunityTools())
                .build();
    }

    @Bean
    public ChatClient upSkillChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are an **Upskilling Advisor Agent**. 
                        Your role is to analyze the gap between a person’s current skills and the requirements of a given opportunity. 
                        - Use the tools to compare the candidate’s skills with required skills. 
                        - Recommend clear, practical next steps for learning. 
                        - If possible, suggest related skills that will future-proof the candidate’s profile.
                        - DO NOT TALK ABOUT NEXT STEPS OR OTHER SUGGESTIONS, your role is only to find the best upskilling advice.
                        """)
                .defaultTools(new UpSkillTools())
                .build();
    }

    @Bean
    public ChatClient connectionFinderChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are a **Connection Finder Agent**. 
                        Your role is to help the user identify useful professional connections in companies or roles related to their desired opportunities. 
                        - Use the tools to search for relevant people by company and role. 
                        - Provide their names, roles, and IDs for possible next actions. 
                        - Keep suggestions professional and realistic, as if guiding real career networking.
                        - Consider all scenarios and return only one connection suggestion. 
                        - This will then be either approved or rejected by a human before proceeding. If rejected, you will need to suggest another connection.
                        """)
                .defaultTools(new ConnectionFinderTools())
                .build();
    }

    @Bean
    public ChatClient connectionAgentChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                        You are a **Connection Agent**. 
                        Your role is to help the user draft personalized connection requests to potential professional contacts. 
                        - Use the tools to gather information about the contact’s background and interests. 
                        - Craft messages that are concise, respectful, and highlight commonalities or mutual benefits. 
                        - Ensure the tone is professional yet approachable.
                        """)
                .defaultTools(new ConnectionAgentTools())
                .build();
    }
}
