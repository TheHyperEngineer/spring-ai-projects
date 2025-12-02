package engineer.hyper.agentic.agent;

import engineer.hyper.agentic.state.State;
import lombok.Getter;
import org.bsc.langgraph4j.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;

import java.util.List;
import java.util.Map;

public abstract class AgentNode implements NodeAction<State> {

    @Getter
    private final String agentName;
    private final ChatClient chatClient;

    public AgentNode(
            String agentName,
            ChatClient chatClient) {
        this.agentName = agentName;
        this.chatClient = chatClient;
    }

    @Override
    public Map<String, Object> apply(State state) throws Exception {

        String input = state.getCurrentMessage();

        //Appending previous messages to the context
        StringBuilder inputWithContext = new StringBuilder();
        List<Map<String, String>> previousMessages = state.getPreviousMessages();
        if (previousMessages.size() > 1) {
            inputWithContext.append("PREVIOUS CONVERSATION:\n");
            for (Map<String, String> message : previousMessages) {
                inputWithContext.append(message.get("role")).append(": ").append(message.get("content")).append("\n");
            }
            inputWithContext.append("END OF PREVIOUS CONVERSATION\n\n");
        }

        //Appending the current input
        inputWithContext
                .append(state.getPreviousAgentKey())
                .append(": ")
                .append(input)
                .append("\n");

        //Invoking the Agent
        String response
                = chatClient.prompt()
                .user(inputWithContext.toString())
                .call()
                .content();

        System.out.println(response);

        //Adding the current message and response to the state
        Map<String, String> newMessage = Map.of(
                "role", agentName,
                "content", response
        );

        previousMessages.add(newMessage);

        return Map.of(
                State.CURRENT_MESSAGE_KEY, response,
                State.PREVIOUS_AGENT_KEY, agentName,
                State.PREVIOUS_MESSAGES_KEY, previousMessages
        );
    }
}
