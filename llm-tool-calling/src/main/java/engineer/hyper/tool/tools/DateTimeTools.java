package engineer.hyper.tool.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class DateTimeTools {

    @Tool(description = "Get the current date in the ISO-8601 format yyyy-MM-dd")
    public String getCurrentDate() {
        log.info("Getting current date");
        return LocalDate.now().toString();
    }

}