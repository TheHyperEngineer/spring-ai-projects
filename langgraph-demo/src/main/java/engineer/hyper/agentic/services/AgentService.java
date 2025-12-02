package engineer.hyper.agentic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AgentService {
    private final ChatClient chatClient;

    public AgentService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
                .defaultSystem("""
                        You are an Weather assistant providing weather information.
                        You use the 'currentWeatherFunction' to get the current weather conditions for a given location.
                        When a user asks for weather information, call the 'currentWeatherFunction' with the location as the argument.
                        The response structure from toll is like this: {
                                                                                 "queryCost": 1,
                                                                                 "latitude": 32.876474,
                                                                                 "longitude": -96.94129,
                                                                                 "resolvedAddress": "75039, USA",
                                                                                 "address": "75039",
                                                                                 "timezone": "America/Chicago",
                                                                                 "tzoffset": -6,
                                                                                 "days": [
                                                                                   {
                                                                                     "tempmax": 5.6,
                                                                                     "tempmin": 3.1,
                                                                                     "temp": 3.7
                                                                                   }
                                                                                 ]
                                                                               }
                        You must evaluate the tool response and respond based on that in following format: 
                                                     {
                                                      "location":  {
                                                      "name": "Dallas",
                                                      "region": "Texas",
                                                      "country": "USA",
                                                      "lat": 32.79,
                                                      "lon": -96.77
                                                      },
                                                      "current":  {
                                                      "temp_f": 75.2,
                                                      "condition":  {
                                                      "text": "Partly cloudy"
                                                      },
                                                      "wind_mph": 10.5,
                                                      "humidity": 65
                                                      }
                                                      } 
                        Provide concise and accurate weather details in your responses.
                        """)
                .defaultToolNames("currentWeatherFunction")
                .build();
    }

    /**
     * Executes a user query by passing it to the chat client and returning the response content.
     *
     * @param input    The user input or query to process.
     * @param messages A list of {@link ToolResponseMessage} providing context or tools for the AI to use.
     * @return The response content as a string.
     */
    public String execute(String input, List<ToolResponseMessage> messages) {
        try {
            // Validate input
            if (input == null || input.isBlank()) {
                throw new IllegalArgumentException("Input query must not be null or empty.");
            }

            // Validate messages
            if (messages == null) {
                throw new IllegalArgumentException("Messages list must not be null.");
            }

            log.info("Executing query: {} with {} tool messages.", input, messages.size());

            BeanOutputConverter<Response> beanOutputConverter = new BeanOutputConverter<>(Response.class);
            String format = beanOutputConverter.getFormat();
            Map<String, Object> vars = Map.of("content", input, "format", format);

            return chatClient
                    .prompt()
                    .user(input)
                    .messages(messages.toArray(ToolResponseMessage[]::new))
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("Error executing query '{}': {}", input, e.getMessage(), e);
            throw new RuntimeException("Failed to process the query. Please try again later.", e);
        }
    }

    /**
     * Response object representing weather information returned by the Weather API.
     *
     * @param location Location details such as city name, region, and country.
     * @param current  Current weather details such as temperature, condition, wind speed, and humidity.
     */
    public record Response(Location location, Current current) {
    }

    /**
     * Represents location details including city, region, country, latitude, and longitude.
     *
     * @param name    City name.
     * @param region  Region name.
     * @param country Country name.
     * @param lat     Latitude.
     * @param lon     Longitude.
     */
    public record Location(String name, String region, String country, Long lat, Long lon) {
    }

    /**
     * Represents current weather conditions including temperature, wind speed, and humidity.
     *
     * @param temp_f    Temperature in Fahrenheit.
     * @param condition Weather condition details.
     * @param wind_mph  Wind speed in miles per hour.
     * @param humidity  Humidity level as a percentage.
     */
    public record Current(String temp_f, Condition condition, String wind_mph, String humidity) {
    }

    /**
     * Represents detailed weather condition such as description.
     *
     * @param text Textual description of the weather condition.
     */
    public record Condition(String text) {
    }
}
