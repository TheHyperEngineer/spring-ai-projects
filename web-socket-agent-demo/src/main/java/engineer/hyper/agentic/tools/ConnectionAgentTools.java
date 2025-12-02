package engineer.hyper.agentic.tools;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConnectionAgentTools {

    @Tool(description = "Get background information about a contact by their ID")
    public String getContactBackground(String personId) throws IOException {
        log.info("Fetching background for person ID: {}", personId);
        // Fake data simulation
        return switch (personId) {
            case "P001" -> "Alice Johnson - Senior Engineer at XYZ Corp, 8 years experience in backend systems.";
            case "P002" -> "Bob Smith - Tech Lead at ABC Ltd, 10 years experience in microservices and cloud.";
            case "P003" -> "Carol White - Engineering Manager at QRS Tech, strong leadership and DevOps expertise.";
            case "P004" -> "David Brown - Software Architect at DataWave, expert in distributed systems and AI.";
            default -> "Unknown contact with ID: " + personId;
        };
    }

    @Tool(description = "Get a contact’s professional interests by their ID")
    public List<String> getContactInterests(String personId) throws IOException {
        log.info("Fetching interests for person ID: {}", personId);
        // Fake data simulation
        List<String> interests = new ArrayList<>();
        switch (personId) {
            case "P001":
                interests.add("Backend performance optimization");
                interests.add("Open-source Java frameworks");
                break;
            case "P002":
                interests.add("Cloud-native applications");
                interests.add("Kubernetes");
                break;
            case "P003":
                interests.add("Team productivity tools");
                interests.add("DevOps practices");
                break;
            case "P004":
                interests.add("AI for system design");
                interests.add("Scalable architectures");
                break;
            default:
                interests.add("General technology and innovation");
        }
        return interests;
    }

    @Tool(description = "Simulate sending a connection request to a contact by ID")
    public String sendConnectionRequest(String personId, String message) throws IOException {
        log.info("Sending connection request to person ID: {}", personId);
        // Fake send action
        return "✅ Connection request sent to " + personId + " with message:\n\n" + message;
    }
}