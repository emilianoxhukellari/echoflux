package echoflux.application.core.jpa.dialog.save;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import lombok.SneakyThrows;
import echoflux.application.core.jpa.core.JpaPropertyCache;
import echoflux.application.core.jpa.dialog.bound_field.JpaSaveDialogFieldFactory;
import echoflux.application.core.jpa.dto.JpaDtoService;
import echoflux.application.core.operation.OperationRunner;
import echoflux.core.core.bean.loader.BeanLoader;

import java.util.List;
import java.util.Objects;

public class JpaSaveCorePropertiesDialog<DTO> extends JpaSaveDialog<DTO> {

    private final Binder<DTO> binder;
    private final JpaDtoService<DTO, ?, ?> service;

    public JpaSaveCorePropertiesDialog(DTO dto,
                                       Class<DTO> beanType,
                                       JpaDtoService<DTO, ?, ?> service,
                                       List<String> excludedProperties,
                                       BeanLoader beanLoader) {
        super(beanType, beanLoader.load(OperationRunner.class));
        this.service = Objects.requireNonNull(service, "Service must not be null");
        this.binder = new Binder<>(beanType);
        this.binder.setBean(dto);

        var form = new FormLayout();

        for (var p : JpaPropertyCache.getCorePropertiesExcluding(beanType, excludedProperties)) {
            form.add(JpaSaveDialogFieldFactory.newBoundField(p, binder, beanType, beanLoader), 2);
        }

        add(form);
    }

    @SneakyThrows({ValidationException.class})
    @Override
    protected DTO save() {
        binder.writeBean(binder.getBean());

        return service.save(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
