package echoflux.core.core.bean.accessor.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import echoflux.core.core.bean.accessor.BeanAccessor;
import echoflux.core.core.validate.guard.Guard;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class BeanAccessorImpl implements BeanAccessor {

    private final ApplicationContext applicationContext;

    @Override
    public <T> Optional<T> findWhen(Class<T> beanType, Predicate<T> predicate) {
        var all = applicationContext.getBeansOfType(beanType)
                .values()
                .stream()
                .filter(predicate)
                .toList();

        Guard.single(all, "Multiple beans of type [%s] found".formatted(beanType.getName()));

        return Optional.of(all.getFirst());
    }

    @Override
    public <T> T getWhen(Class<T> beanType, Predicate<T> predicate) {
        return findWhen(beanType, predicate).orElseThrow(
                () -> new NoSuchElementException("No bean of type [%s] found".formatted(beanType.getName()))
        );
    }

    @Override
    public <T> T get(Class<T> beanType) {
        return applicationContext.getBean(beanType);
    }

}
