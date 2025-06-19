package echoflux.application.template;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import echoflux.application.core.jpa.grid.JpaGrid;
import echoflux.application.core.operation.OperationRunner;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.domain.template.data.TemplateEntity_;
import echoflux.domain.template.data.TemplateProjection;
import echoflux.domain.template.data.TemplateRepository;
import echoflux.domain.template.service.TemplateService;
import jakarta.annotation.security.RolesAllowed;
import echoflux.application.layout.MainLayout;

@PageTitle("Templates")
@Route(value = "templates", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class TemplatesView extends VerticalLayout {

    public TemplatesView(TemplateRepository templateRepository,
                         TemplateService templateService,
                         BeanLoader beanLoader) {
        var grid = new JpaGrid<>(TemplateProjection.class, templateRepository);
        grid.addColumnWithFilter(TemplateEntity_.NAME);
        grid.addColumnWithFilter(TemplateEntity_.CONTENT)
                .setWidth("400px");
        grid.addIdColumnWithFilter();
        grid.addAuditColumnsWithFilter();
        grid.addContextMenuItemWithDoubleClickListener(
                "Edit",
                item -> SaveTemplateDialog.newUpdate(item, beanLoader)
                        .withSaveListener(grid::refreshItemById)
                        .open()
        );
        grid.addConfirmContextMenuItem(
                "Delete",
                item -> OperationRunner.run(
                        "Deleting template with ID [%s]".formatted(item.getId()),
                        () -> templateService.deleteById(item.getId()),
                        grid::refreshAll
                )
        );

        var controls = grid.withControls();
        controls.addCreateButton(
                () -> SaveTemplateDialog.newCreate(beanLoader)
                .withSaveListener(_ -> grid.refreshAll())
                .open()
        );
        addAndExpand(controls);
    }

}
