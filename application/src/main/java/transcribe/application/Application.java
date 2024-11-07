package transcribe.application;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import transcribe.core.core.initialize.Initialize;

import java.util.Comparator;
import java.util.List;

@Theme(value = "transcribe", variant = Lumo.LIGHT)
@Push
@SpringBootApplication
@ComponentScan({
        "transcribe.application",
        "transcribe.core",
        "transcribe.domain",
        "transcribe.template"
})
@EnableJpaRepositories("transcribe.domain")
@EntityScan("transcribe.domain")
@EnableScheduling
@RequiredArgsConstructor
public class Application implements AppShellConfigurator {

    private final List<Initialize> initializeList;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        initializeList.stream()
                .sorted(Comparator.comparing(Initialize::getOrder))
                .forEach(Initialize::initialize);
    }

}
