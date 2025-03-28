package transcribe.application.core.init;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.application.core.dialog.Dialogs;
import transcribe.application.core.ui.UiUtils;

@Slf4j
@Component
public class ServiceListener implements VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(this::initSession);
        event.getSource().addUIInitListener(this::initUI);
    }

    private void initSession(SessionInitEvent event) {
        event.getSession().setErrorHandler(e -> {
                    log.error("Session error: ", e.getThrowable());
                    UiUtils.safeAccess(UI.getCurrent(), () -> Dialogs.error(e.getThrowable()));
                }
        );

        log.info("Session initialized");
    }

    private void initUI(UIInitEvent event) {
        var configuration = event.getUI().getLoadingIndicatorConfiguration();

        configuration.setFirstDelay(250);
        configuration.setSecondDelay(600);
        configuration.setThirdDelay(2000);
    }

}
