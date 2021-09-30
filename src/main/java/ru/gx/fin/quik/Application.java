package ru.gx.fin.quik;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import ru.gx.fin.quik.events.DbAdapterStartEvent;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        final var context = new SpringApplicationBuilder(Application.class)
                .web(WebApplicationType.NONE)
                .run(args);
        context.publishEvent(new DbAdapterStartEvent("Application"));
    }
}
