package transcribe.application.core.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class SpringContext implements ApplicationContextAware {

    private final static AtomicReference<ApplicationContext> contextReference = new AtomicReference<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        contextReference.set(applicationContext);
    }

    public static ApplicationContext get() {
        return Objects.requireNonNull(contextReference.get(), "Spring context not initialized");
    }

    public static <T> T getBean(Class<T> beanClass) {
        return get().getBean(beanClass);
    }

}
