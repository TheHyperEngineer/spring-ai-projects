package engineer.hyper.agentic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;

import java.util.Random;

@Slf4j
public class MyTools {

    final int[] temperatures = {-125, 15, -255};
    private final Random random = new Random();

    @Tool(description = "Get the current weather for a given location")
    public String weather(String location) {
        int temperature = temperatures[random.nextInt(temperatures.length)];
        log.info(">>> Tool Call responseTemp: {}", temperature);
        return "The current weather in " + location + " is sunny with a temperature of " + temperature + "°C.";
    }
}