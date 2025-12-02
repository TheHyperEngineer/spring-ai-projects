package engineer.hyper.agentic.state;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class State extends AgentState {

    public static final String PREVIOUS_AGENT_KEY = "current_agent";
    public static final String CURRENT_MESSAGE_KEY = "current_message";
    public static final String PREVIOUS_MESSAGES_KEY = "previous_messages";

    public static final Map<String, Channel<?>> SCHEMA = Map.of(
            PREVIOUS_AGENT_KEY, Channels.base(() -> ""),
            CURRENT_MESSAGE_KEY, Channels.base(() -> ""),
            PREVIOUS_MESSAGES_KEY, Channels.base(() -> new ArrayList<Map<String, String>>())
    );

    public State(Map<String, Object> initData) {
        super(initData);
    }

    public List<Map<String, String>> getPreviousMessages() {
        return this.<List<Map<String, String>>>value(PREVIOUS_MESSAGES_KEY).orElse(new ArrayList<>());
    }

    public String getPreviousAgentKey() {
        return this.<String>value(PREVIOUS_AGENT_KEY).orElse("NO AGENT");
    }

    public String getCurrentMessage() {
        return this.<String>value(CURRENT_MESSAGE_KEY).orElse("");
    }
}