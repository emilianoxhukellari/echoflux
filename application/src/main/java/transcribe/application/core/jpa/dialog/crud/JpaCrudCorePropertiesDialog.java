package transcribe.application.core.jpa.dialog.crud;

import com.vaadin.flow.data.binder.Binder;
import org.apache.commons.lang3.Validate;
import org.springframework.data.jpa.repository.JpaRepository;
import transcribe.application.core.jpa.core.CoreAttributePropertySet;
import transcribe.application.core.jpa.dialog.bound_field.JpaCrudDialogFieldFactory;
import transcribe.domain.bean.BeanUtils;

import java.util.List;


public class JpaCrudCorePropertiesDialog<T> extends JpaCrudDialog<T> {

    private final Binder<T> binder;
    private final JpaRepository<T, ?> repository;

    public JpaCrudCorePropertiesDialog(T entity, Class<T> beanType, JpaRepository<T, ?> repository, List<String> excludedProperties) {
        super(BeanUtils.getIdFieldValue(entity, beanType) == null ? JpaCrudDialogMode.NEW : JpaCrudDialogMode.EDIT, beanType);
        this.repository = Validate.notNull(repository, "Repository must not be null");
        this.binder = new Binder<>(beanType);
        this.binder.setBean(entity);

        CoreAttributePropertySet.getExcluding(beanType, excludedProperties)
                .getProperties()
                .forEach(p -> form.add(JpaCrudDialogFieldFactory.newBoundField(p, binder)));
    }

    @Override
    protected void save() {
        repository.saveAndFlush(binder.getBean());
    }

    @Override
    protected void delete() {
        repository.delete(binder.getBean());
    }

    @Override
    protected boolean validate() {
        return binder.validate().isOk();
    }
}
