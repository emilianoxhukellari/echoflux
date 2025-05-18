package echoflux.application.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import echoflux.application.layout.MainLayout;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
@Slf4j
public class HomeView extends Composite<VerticalLayout> {

    public HomeView() {
    }

}
