package engineer.hyper.agentic.executors;

import engineer.hyper.agentic.services.AgentService;
import engineer.hyper.agentic.state.State;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AgentExecutor {
    private final AgentService agentService;


    /**
     * Calls the Weather Agent to retrieve weather information based on user input.
     *
     * @param state The current state of the workflow.
     * @return A map containing the weather details as output.
     */
    public Map<String, Object> callWeatherAgent(State state) {
        log.info("callWeatherAgent: {}", state);

        log.info("Weather Agent Input: {}", state.getInput());

        var query = (String) state.getInput().get("query");
        String response = agentService.execute(query, List.of());

        Map<String, Object> output = new HashMap<>();
        output.put("weather", response);
        output.put("call_travel_agent", true);
        log.info("Weather Agent Output: {}", output);

        return Map.of(State.OUTPUT, output);
    }

    /**
     * Calls the Travel Agent to provide travel recommendations based on the weather.
     *
     * @param state The current state of the workflow.
     * @return A map containing travel recommendations as output.
     */
    public Map<String, Object> callTravelAgent(State state) {
        log.info("callTravelAgent: {}", state);

        var weather = (String) state.getOutput().get("weather");

        String recommendation;
        if (weather.contains("rain")) {
            recommendation = "Visit indoor attractions like museums, art galleries, or enjoy a day at the mall.";
        } else if (weather.contains("cloudy")) {
            recommendation = "Consider activities like visiting an aquarium, a science center, or enjoying a cozy café.";
        } else if (weather.contains("storm") || weather.contains("thunder")) {
            recommendation = "Stay safe indoors. Enjoy a good book, watch a movie, or try indoor yoga.";
        } else if (weather.contains("snow")) {
            recommendation = "Explore indoor winter activities like skating in an indoor rink or sipping hot chocolate by a fireplace.";
        } else if (weather.contains("misty")) {
            recommendation = "Visit indoor attractions like museums, art galleries, or enjoy a day at the mall.";
        } else {
            recommendation = "Enjoy outdoor activities like hiking, biking, or a picnic in the park!";
        }

        Map<String, Object> output = new HashMap<>();
        output.put("recommendation", recommendation);
        log.info("Travel Agent Output: {}", output);

        return Map.of(State.MID, output);
    }

    /**
     * Calls the Food Agent to provide food suggestions based on travel recommendations.
     *
     * @param state The current state of the workflow.
     * @return A map containing food suggestions as output.
     */
    public Map<String, Object> callFoodAgent(State state) {
        log.info("callFoodAgent: {}", state);

        var recommendation = (String) state.getMID().get("recommendation");

        String foodSuggestion;
        if (recommendation.contains("outdoor")) {
            foodSuggestion = "Pack some easy-to-carry snacks like sandwiches, granola bars, and fresh fruit. Don't forget plenty of water!";
        } else if (recommendation.contains("cloudy")) {
            foodSuggestion = "Warm up with comfort food like soups, hot beverages, or enjoy a cozy brunch at a nearby bakery.";
        } else if (recommendation.contains("snow") || recommendation.contains("rain") || recommendation.contains("misty")) {
            foodSuggestion = "Enjoy hearty meals like stews, hot chocolate, or baked goods to keep you warm.";
        } else {
            foodSuggestion = "Explore the local street food scene or grab a quick bite from food trucks in the area.";
        }

        Map<String, Object> output = new HashMap<>();
        output.put("food", foodSuggestion);
        log.info("Food Agent Output: {}", output);

        return Map.of(State.FOOD, output);
    }
}
