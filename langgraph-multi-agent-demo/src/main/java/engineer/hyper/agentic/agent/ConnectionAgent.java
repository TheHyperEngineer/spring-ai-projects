package engineer.hyper.agentic.agent;

import org.springframework.ai.chat.client.ChatClient;

public class ConnectionAgent extends AgentNode {
    public ConnectionAgent(ChatClient connectionAgentChatClient) {
        super(Agents.CONNECTION_AGENT.name(), connectionAgentChatClient);
    }
}
