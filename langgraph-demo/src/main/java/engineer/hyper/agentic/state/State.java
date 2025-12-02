package engineer.hyper.agentic.state;

import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import java.util.HashMap;
import java.util.Map;

public class State extends AgentState {

    public static final String INPUT = "question";
    public static final String MID = "recommendation";
    public static final String OUTPUT = "weather";
    public static final String FOOD = "food";
    public static final String CALL_TRAVEL_AGENT = "call_travel_agent";

    public static Map<String, Channel<?>> SCHEMA = Map.of(
            INPUT, Channels.base(() -> new HashMap<>()),
            OUTPUT, Channels.base(() -> new HashMap<>()),
            MID, Channels.base(() -> new HashMap<>()),
            CALL_TRAVEL_AGENT, Channels.base(() -> Boolean.TRUE)
    );

    public State(Map<String, Object> initData) {
        super(initData);
    }

    public Map<String, Object> getInput() {
        return this.<Map<String, Object>>value(INPUT).orElseGet(HashMap::new);
    }

    public Map<String, Object> getOutput() {
        return this.<Map<String, Object>>value(OUTPUT).orElseGet(HashMap::new);
    }

    public Map<String, Object> getMID() {
        return this.<Map<String, Object>>value(MID).orElseGet(HashMap::new);
    }

    public boolean getCallTravelAgent() {
        return this.<Boolean>value(CALL_TRAVEL_AGENT).orElse(Boolean.TRUE);
    }
}
