package engineer.hyper.agentic.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Configuration
public class FunctionConfiguration {

    private final WeatherConfigProperties props;

    public FunctionConfiguration(WeatherConfigProperties props) {
        this.props = props;
    }

    @Bean
    @Description("Get the current weather conditions for the given country.")
    public Function<WeatherService.Request, String> currentWeatherFunction() {
        return new WeatherService(props);
    }

}