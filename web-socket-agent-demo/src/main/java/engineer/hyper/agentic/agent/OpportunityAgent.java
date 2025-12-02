package engineer.hyper.agentic.agent;

import org.springframework.ai.chat.client.ChatClient;

public class OpportunityAgent extends AgentNode {

    public OpportunityAgent(ChatClient opportunityChatClient) {
        super(Agents.OPPORTUNITY_AGENT.name(), opportunityChatClient);
    }
}
