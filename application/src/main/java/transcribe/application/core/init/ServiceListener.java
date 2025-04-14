package transcribe.application.core.init;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import transcribe.application.core.dialog.TsDialogs;
import transcribe.application.core.ui.UiUtils;
import transcribe.application.security.AuthenticatedUser;

@Slf4j
@Component
@RequiredArgsConstructor
public class ServiceListener implements VaadinServiceInitListener {

    private final AuthenticatedUser authenticatedUser;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addSessionInitListener(this::initSession);
        event.getSource().addUIInitListener(this::initUI);
    }

    private void initSession(SessionInitEvent event) {
        event.getSession().setErrorHandler(e -> {
                    log.error("Session error: ", e.getThrowable());
                    UiUtils.safeAccess(
                            UI.getCurrent(),
                            () -> TsDialogs.error(e.getThrowable(), authenticatedUser)
                    );
                }
        );
    }

    private void initUI(UIInitEvent event) {
        var configuration = event.getUI().getLoadingIndicatorConfiguration();

        configuration.setFirstDelay(250);
        configuration.setSecondDelay(600);
        configuration.setThirdDelay(2000);
    }

}
