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
public class ConnectionFinderTools {

    public static class Person {
        public String personId;
        public String name;
        public String role;
        public String company;

        public Person(String personId, String name, String role, String company) {
            this.personId = personId;
            this.name = name;
            this.role = role;
            this.company = company;
        }

        @Override
        public String toString() {
            return name + " (" + role + " at " + company + ") [ID: " + personId + "]";
        }
    }

    @Tool(description = "Find people in a company")
    public List<Person> findPeopleInCompany(String companyName) throws IOException {
        log.info("Finding people in company: {}", companyName);
        // Fake data simulation
        List<Person> people = new ArrayList<>();
        people.add(new Person("P001", "Alice Johnson", "Senior Engineer", companyName));
        people.add(new Person("P002", "Bob Smith", "Tech Lead", companyName));
        people.add(new Person("P003", "Carol White", "Engineering Manager", companyName));
        people.add(new Person("P004", "David Brown", "Software Architect", companyName));
        return people;
    }
}