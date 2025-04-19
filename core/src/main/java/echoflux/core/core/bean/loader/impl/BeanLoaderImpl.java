package echoflux.core.core.bean.loader.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import echoflux.core.core.bean.loader.BeanLoader;
import echoflux.core.core.validate.guard.Guard;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class BeanLoaderImpl implements BeanLoader {

    private final ApplicationContext applicationContext;

    @Override
    public <T> Optional<T> findWhen(Class<T> beanType, Predicate<T> predicate) {
        var all = applicationContext.getBeansOfType(beanType)
                .values()
                .stream()
                .filter(predicate)
                .toList();

        Guard.singleElement(all, "More than one bean of type [%s] found", beanType.getName());

        return Optional.of(all.getFirst());
    }

    @Override
    public <T> T loadWhen(Class<T> beanType, Predicate<T> predicate) {
        return findWhen(beanType, predicate).orElseThrow(
                () -> new NoSuchElementException("No bean of type [%s] found".formatted(beanType.getName()))
        );
    }

    @Override
    public <T> T load(Class<T> beanType) {
        var names = applicationContext.getBeanNamesForType(beanType);

        Guard.singleElement(names, "More than one bean of type [%s] found", beanType.getName());

        return applicationContext.getBean(names[0], beanType);
    }

}
