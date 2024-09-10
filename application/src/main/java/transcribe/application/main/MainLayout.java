package transcribe.application.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import transcribe.application.home.HomeView;
import transcribe.application.operation.OperationsView;
import transcribe.application.security.AuthenticatedUser;
import transcribe.application.transcribe.TranscribeView;
import transcribe.application.user.UsersView;

public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H1();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        var appName = new Span("transcribee.");
        appName.addClassNames(LumoUtility.FontWeight.EXTRABOLD, LumoUtility.FontSize.XXLARGE);
        var header = new Header(appName);
        var scroller = new Scroller(newSideNav());

        addToDrawer(header, scroller, newFooter());
    }

    private SideNav newSideNav() {
        var nav = new SideNav();

        addIfHasAccess(nav, HomeView.class, "Home", LineAwesomeIcon.HOME_SOLID.create());
        addIfHasAccess(nav, TranscribeView.class, "Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        addIfHasAccess(nav, UsersView.class, "Users", LineAwesomeIcon.USERS_SOLID.create());
        addIfHasAccess(nav, OperationsView.class, "Operations", LineAwesomeIcon.COGS_SOLID.create());

        return nav;
    }

    private Footer newFooter() {
        var footer = new Footer();

        var user = authenticatedUser.get();
        if (user.isPresent()) {
            var userMenu = new MenuBar();
            userMenu.addThemeVariants(MenuBarVariant.LUMO_CONTRAST, MenuBarVariant.LUMO_TERTIARY_INLINE);

            var userSpan = new HorizontalLayout(new Text(user.get().getName()), LumoIcon.DROPDOWN.create());
            userSpan.getStyle().set("gap", "var(--lumo-space-xs)");
            var userItem = userMenu.addItem(userSpan);

            userItem.getSubMenu().addItem("Sign out", _ -> authenticatedUser.logout());
            footer.add(userMenu);
        } else {
            var loginLink = new Anchor("login", "Sign in");
            footer.add(loginLink);
        }

        return footer;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        return StringUtils.stripToEmpty(getContent().getClass().getAnnotation(PageTitle.class).value());
    }

    private <T extends AbstractIcon<T>> void addIfHasAccess(SideNav nav,
                                                            Class<? extends Component> viewClass,
                                                            String caption,
                                                            AbstractIcon<T> icon) {
        if (accessChecker.hasAccess(viewClass)) {
            nav.addItem(new SideNavItem(caption, viewClass, icon));
        }
    }

}
