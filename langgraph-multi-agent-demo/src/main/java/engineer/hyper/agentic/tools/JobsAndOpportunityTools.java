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
public class JobsAndOpportunityTools {

    @Tool(description = "Get all skills of a person")
    public List<String> getSkillsOfPerson(String name) throws IOException {
        log.info("Fetching skills for person: {}", name);
        List<String> skills = new ArrayList<>();
        skills.add("Java");
        skills.add("Spring Boot");
        skills.add("React");
        skills.add("SQL");
        skills.add("Docker");
        skills.add("Microservices");
        return skills;
    }

    @Tool(description = "Find opportunities for given skills")
    public List<String> findOpportunitiesForSkills(List<String> skills) throws IOException {
        log.info("Finding opportunities for skills: {}", skills);
        List<String> opportunities = new ArrayList<>();
        opportunities.add("Backend Developer at XYZ Corp - Requires Java, Spring Boot, Docker");
        opportunities.add("Fullstack Engineer at ABC Ltd - Requires React, Node.js, SQL");
        opportunities.add("Cloud Engineer at QRS Tech - Requires Kubernetes, AWS, Microservices");
        opportunities.add("Data Engineer at DataWave - Requires SQL, Spark, Python");
        return opportunities;
    }
}