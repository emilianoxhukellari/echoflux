package transcribe.application.core.jpa.dialog.save;

import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.data.binder.Binder;
import org.springframework.data.jpa.repository.JpaRepository;
import transcribe.application.core.jpa.core.CoreAttributePropertySet;
import transcribe.application.core.jpa.dialog.bound_field.JpaSaveDialogFieldFactory;

import java.util.List;
import java.util.Objects;

public class JpaSaveCorePropertiesDialog<T> extends JpaSaveDialog<T> {

    private final Binder<T> binder;
    private final JpaRepository<T, ?> repository;

    public JpaSaveCorePropertiesDialog(T entity,
                                       Class<T> beanType,
                                       JpaRepository<T, ?> repository,
                                       List<String> excludedProperties) {
        super(beanType);
        this.repository = Objects.requireNonNull(repository, "Repository must not be null");
        this.binder = new Binder<>(beanType);
        this.binder.setBean(entity);

        var form = new FormLayout();
        CoreAttributePropertySet.getExcluding(beanType, excludedProperties)
                .getProperties()
                .forEach(p -> form.add(JpaSaveDialogFieldFactory.newBoundField(p, binder), 2));

        add(form);
    }

    @Override
    protected T save() {
        return repository.saveAndFlush(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }

}
