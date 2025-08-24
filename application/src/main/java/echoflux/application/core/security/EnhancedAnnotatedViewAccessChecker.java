package echoflux.application.core.security;

import com.vaadin.flow.router.internal.RouteUtil;
import com.vaadin.flow.server.auth.AccessCheckResult;
import com.vaadin.flow.server.auth.NavigationAccessChecker;
import com.vaadin.flow.server.auth.NavigationContext;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.RequiredArgsConstructor;

@SpringComponent
@RequiredArgsConstructor
public class EnhancedAnnotatedViewAccessChecker implements NavigationAccessChecker {

    @Override
    public AccessCheckResult check(NavigationContext context) {
        var targetView = context.getNavigationTarget();

        if (RouteUtil.isAutolayoutEnabled(targetView, context.getLocation().getPath())) {
            var registry = context.getRouter().getRegistry();
            boolean noParents = registry.getRegisteredRoutes()
                    .stream()
                    .filter(rd -> rd.getNavigationTarget().equals(targetView))
                    .map(d -> d.getParentLayouts().isEmpty())
                    .findFirst()
                    .orElse(true);

            if (noParents && registry.hasLayout(context.getLocation().getPath())) {
                var layout = registry.getLayout(context.getLocation().getPath());
                boolean hasAccess = EnhancedAccessAnnotationChecker.hasAccess(layout, context.getPrincipal());

                if (!hasAccess) {
                    return context.deny("You do not have access to this layout.");
                }
            }
        }

        boolean hasAccess = EnhancedAccessAnnotationChecker.hasAccess(targetView, context.getPrincipal());

        if (hasAccess) {
            return context.allow();
        }

        return context.deny("You do not have access to this view.");
    }

}
