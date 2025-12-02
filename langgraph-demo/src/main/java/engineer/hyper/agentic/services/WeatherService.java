package engineer.hyper.agentic.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

import java.util.function.Function;

/**
 * Service for interacting with the Weather API to fetch current weather information.
 * Documentation: https://www.weatherapi.com/api-explorer.aspx
 */
@Slf4j
public class WeatherService implements Function<WeatherService.Request, String> {

    private final RestClient restClient;
    private final WeatherConfigProperties weatherProps;

    /**
     * Constructor for initializing the WeatherService with configuration properties.
     *
     * @param props WeatherConfigProperties containing API URL and API key.
     */
    public WeatherService(WeatherConfigProperties props) {
        this.weatherProps = props;
        log.debug("Weather API URL: {}", weatherProps.apiUrl());
        log.debug("Weather API Key: {}", weatherProps.apiKey());
        this.restClient = RestClient.create(weatherProps.apiUrl());
    }

    /**
     * Fetches current weather information for a given city using the Weather API.
     *
     * @param weatherRequest Request object containing the city name.
     * @return Response object containing the current weather details.
     */
    @Override
    public String apply(Request weatherRequest) {
        try {
            log.info("Weather Request: {}", weatherRequest);

            String response = restClient.get()
                    .retrieve()
                    .body(String.class);

            log.info("Weather API Response: {}", response);
            return response;
        } catch (Exception e) {
            log.error("Failed to fetch weather information for city: {}", weatherRequest.city(), e);
            throw new RuntimeException("Error fetching weather information. Please try again later.", e);
        }
    }

    /**
     * Request object representing the city for which weather information is requested.
     *
     * @param city Name of the city.
     */
    public record Request(String city) {
    }
}