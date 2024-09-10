package transcribe.application.home;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import transcribe.application.main.MainLayout;

@PageTitle("Home")
@Route(value = "", layout = MainLayout.class)
@PermitAll
public class HomeView extends Composite<VerticalLayout> {

}
