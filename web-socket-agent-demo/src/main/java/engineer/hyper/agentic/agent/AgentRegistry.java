package engineer.hyper.agentic.agent;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class AgentRegistry {

    private final List<AgentNode> agents = new ArrayList<>();
    @Autowired
    @Qualifier("connectionFinderChatClient")
    private ChatClient connectionFinderChatClient;

    @Autowired
    @Qualifier("opportunityChatClient")
    private ChatClient opportunityChatClient;

    @Autowired
    @Qualifier("connectionAgentChatClient")
    private ChatClient connectionAgentChatClient;

    @Autowired
    @Qualifier("upSkillChatClient")
    private ChatClient upSkillChatClient;

    @PostConstruct
    public void initialize() {
        System.out.println("Initializing Agent Registry...");
        AgentNode opportunityAgent = new OpportunityAgent(opportunityChatClient);
        AgentNode upskillAgent = new UpSkillAgent(upSkillChatClient);
        AgentNode connectionFinderAgent = new ConnectionFinderAgent(connectionFinderChatClient);
        AgentNode connectionAgent = new ConnectionAgent(connectionAgentChatClient);

        agents.add(opportunityAgent);
        agents.add(upskillAgent);
        agents.add(connectionFinderAgent);
        agents.add(connectionAgent);
        System.out.println("Registered Agents: " + agents.stream().map(AgentNode::getAgentName).toList());
    }

    public AgentNode get(String agentName) {
        Optional<AgentNode> agent = agents.stream()
                .filter(a -> a.getAgentName().equals(agentName))
                .findFirst();
        return agent.orElseThrow(() -> new RuntimeException("Agent not found: " + agentName));
    }
}
