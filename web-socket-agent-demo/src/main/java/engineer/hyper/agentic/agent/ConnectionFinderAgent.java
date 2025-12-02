package engineer.hyper.agentic.agent;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
public class ConnectionFinderAgent extends AgentNode {

    public ConnectionFinderAgent(ChatClient connectionFinderChatClient) {
        super(Agents.CONNECTION_FINDER_AGENT.name(), connectionFinderChatClient);
    }
}
