package transcribe.application.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import transcribe.core.core.bean.loader.BeanLoader;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.Supplier;

@Component
public class SpringContext implements ApplicationContextAware {

    private final static AtomicReference<ApplicationContext> REF = new AtomicReference<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        REF.set(applicationContext);
    }

    public static ApplicationContext get() {
        return Objects.requireNonNull(REF.get(), "Spring context not initialized");
    }

    public static void runTransactional(Runnable runnable) {
        getBean(TransactionTemplate.class)
                .executeWithoutResult(_ -> runnable.run());
    }

    public static <T> T getTransactional(Supplier<T> supplier) {
        return getTransactional(supplier, false);
    }

    public static <T> T getTransactionalReadonly(Supplier<T> supplier) {
        return getTransactional(supplier, true);
    }

    public static <T> T getTransactional(Supplier<T> supplier, boolean readOnly) {
        var transactionTemplate = getBean(TransactionTemplate.class);
        transactionTemplate.setReadOnly(readOnly);

        return transactionTemplate.execute(_ -> supplier.get());
    }

    public static <T> T getBean(Class<T> beanType) {
        return get().getBean(beanType);
    }

    public static <T> Optional<T> findBeanWhen(Class<T> beanType, Predicate<T> predicate) {
        return getBean(BeanLoader.class).findWhen(beanType, predicate);
    }

    public static <T> T loadBeanWhen(Class<T> beanType, Predicate<T> predicate) {
        return getBean(BeanLoader.class).loadWhen(beanType, predicate);
    }

}
