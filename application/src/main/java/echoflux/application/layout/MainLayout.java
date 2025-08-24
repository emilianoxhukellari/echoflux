package echoflux.application.layout;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.AbstractIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.spring.security.AuthenticationContext;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import echoflux.application.access_management.permission.PermissionsView;
import echoflux.application.core.security.EnhancedAccessAnnotationChecker;
import echoflux.application.access_management.role.RolesView;
import org.apache.commons.lang3.StringUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;
import echoflux.application.completion.CompletionsView;
import echoflux.application.home.HomeView;
import echoflux.application.settings.SettingsView;
import echoflux.application.template.TemplatesView;
import echoflux.application.transcribe.TranscribeView;
import echoflux.application.transcription.TranscriptionsView;
import echoflux.application.transcription_word.TranscriptionWordsView;
import echoflux.application.access_management.application_user.ApplicationUsersView;

@Layout
public class MainLayout extends AppLayout {

    private H1 viewTitle;

    private final AuthenticationContext authenticationContext;

    public MainLayout(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;

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
        var appName = new Span("echoflux");
        appName.addClassNames(LumoUtility.FontWeight.EXTRABOLD, LumoUtility.FontSize.XXLARGE);
        var header = new Header(appName);
        var scroller = new Scroller(newSideNav());

        addToDrawer(header, scroller, newFooter());
    }

    private SideNav newSideNav() {
        var nav = new SideNav();

        //todo: add nested items
        addIfHasAccess(nav, HomeView.class, "Home", LineAwesomeIcon.HOME_SOLID.create());
        addIfHasAccess(nav, TranscribeView.class, "Transcribe", LineAwesomeIcon.PODCAST_SOLID.create());
        addIfHasAccess(nav, TranscriptionsView.class, "Transcriptions", LineAwesomeIcon.FILE_AUDIO_SOLID.create());
        addIfHasAccess(nav, CompletionsView.class, "Completions", LineAwesomeIcon.CHECK_CIRCLE_SOLID.create());
        addIfHasAccess(nav, TranscriptionWordsView.class, "Transcription Words", LineAwesomeIcon.FONT_SOLID.create());
        addIfHasAccess(nav, TemplatesView.class, "Templates", LineAwesomeIcon.FILE_CODE_SOLID.create());
        addIfHasAccess(nav, ApplicationUsersView.class, "Users", LineAwesomeIcon.USERS_SOLID.create());
        addIfHasAccess(nav, RolesView.class, "Roles", LineAwesomeIcon.USER_SHIELD_SOLID.create());
        addIfHasAccess(nav, PermissionsView.class, "Permissions", LineAwesomeIcon.KEY_SOLID.create());
        addIfHasAccess(nav, SettingsView.class, "Settings", LineAwesomeIcon.COG_SOLID.create());

        return nav;
    }

    private Footer newFooter() {
        var footer = new Footer();

        var principalName = authenticationContext.getPrincipalName();
        if (principalName.isPresent()) {
            var userMenu = new MenuBar();
            userMenu.addThemeVariants(MenuBarVariant.LUMO_CONTRAST, MenuBarVariant.LUMO_TERTIARY_INLINE);

            var userSpan = new HorizontalLayout(new Text(principalName.get()), LumoIcon.DROPDOWN.create());
            userSpan.getStyle().set("gap", "var(--lumo-space-xs)");
            var userItem = userMenu.addItem(userSpan);

            userItem.getSubMenu().addItem("Sign out", _ -> authenticationContext.logout());
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
        if (getContent() instanceof HasDynamicTitle hasDynamicTitle) {
            return hasDynamicTitle.getPageTitle();
        }

        var pageTitleAnnotation = getContent().getClass().getAnnotation(PageTitle.class);

        return pageTitleAnnotation != null
                ? pageTitleAnnotation.value()
                : StringUtils.EMPTY;
    }

    private <T extends AbstractIcon<T>> void addIfHasAccess(SideNav nav,
                                                            Class<? extends Component> viewClass,
                                                            String caption,
                                                            AbstractIcon<T> icon) {
        if (EnhancedAccessAnnotationChecker.hasAccess(viewClass)) {
            nav.addItem(new SideNavItem(caption, viewClass, icon));
        }
    }

}
