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
public class UpSkillTools {

    @Tool(description = "Get required skills for a specific opportunity")
    public List<String> getSkillsRequiredForOpportunity(String opportunity) throws IOException {
        log.info("Fetching required skills for opportunity: {}", opportunity);
        // Fake data simulation (using IDs as keys)
        List<String> requiredSkills = new ArrayList<>();
        requiredSkills.add("SQL");
        requiredSkills.add("Docker");
        requiredSkills.add("Python");
        requiredSkills.add("Java");
        return requiredSkills;
    }

    @Tool(description = "Get skills of a person (for comparison with required skills)")
    public List<String> getSkillsOfPerson(String name) throws IOException {
        log.info("Fetching skills for person: {}", name);
        // Fake data simulation
        List<String> skills = new ArrayList<>();
        skills.add("Java");
        skills.add("Spring Boot");
        skills.add("React");
        skills.add("SQL");
        skills.add("Docker");
        skills.add("Microservices");
        return skills;
    }
}