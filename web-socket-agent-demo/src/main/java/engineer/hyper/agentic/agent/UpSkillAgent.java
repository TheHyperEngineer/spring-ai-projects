package engineer.hyper.agentic.agent;

import org.springframework.ai.chat.client.ChatClient;

public class UpSkillAgent extends AgentNode {
    public UpSkillAgent(ChatClient upSkillChatClient) {
        super(Agents.UPSKILL_AGENT.name(), upSkillChatClient);
    }
}
