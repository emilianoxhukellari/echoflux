package transcribe.application.core.jpa.dialog.save;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import transcribe.application.core.jpa.core.JpaPropertyCache;
import transcribe.application.core.jpa.dialog.bound_field.JpaSaveDialogFieldFactory;
import transcribe.application.core.jpa.dto.JpaDtoService;

import java.util.List;
import java.util.Objects;

public class JpaSaveCorePropertiesDialog<DTO> extends JpaSaveDialog<DTO> {

    private final Binder<DTO> binder;
    private final JpaDtoService<DTO, ?, ?> service;

    public JpaSaveCorePropertiesDialog(DTO dto,
                                       Class<DTO> beanType,
                                       JpaDtoService<DTO, ?, ?> service,
                                       List<String> excludedProperties) {
        super(beanType);
        this.service = Objects.requireNonNull(service, "Service must not be null");
        this.binder = new Binder<>(beanType);
        this.binder.setBean(dto);

        var form = new FormLayout();

        for (var p : JpaPropertyCache.getCorePropertiesExcluding(beanType, excludedProperties)) {
            form.add(JpaSaveDialogFieldFactory.newBoundField(p, binder, beanType), 2);
        }

        add(form);
    }

    @Override
    protected DTO save() {
        //todo: fix bug where entityBeanType is updated in UI
        return service.save(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
