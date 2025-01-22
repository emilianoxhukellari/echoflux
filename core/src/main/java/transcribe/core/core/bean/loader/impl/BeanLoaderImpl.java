package transcribe.core.core.bean.loader.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import transcribe.core.core.bean.loader.BeanLoader;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Predicate;

@Component
@RequiredArgsConstructor
public class BeanLoaderImpl implements BeanLoader {

    private final ApplicationContext applicationContext;

    @Override
    public <T> T loadWhen(Class<T> beanType, Predicate<T> predicate) {
        return Arrays.stream(applicationContext.getBeanNamesForType(beanType))
                .map(name -> applicationContext.getBean(name, beanType))
                .filter(predicate)
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No bean of type [%s] found".formatted(beanType.getName())));
    }

}
