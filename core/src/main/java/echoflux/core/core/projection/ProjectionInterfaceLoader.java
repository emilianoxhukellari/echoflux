package echoflux.core.core.projection;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.function.Failable;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import echoflux.annotation.core.ObjectConvertable;
import echoflux.annotation.projection.ProjectionInterface;
import echoflux.core.core.utils.EfFunctions;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ProjectionInterfaceLoader {

    private final static Map<Class<?>, Class<?>> CACHE = scanAll();

    /**
     * Loads an already cached {@link ProjectionInterface} for the given bean type.
     * */
    public static  <T> Class<? extends ObjectConvertable<T>> load(Class<T> beanType) {
        @SuppressWarnings("unchecked")
        var projectionInterface = (Class<? extends ObjectConvertable<T>>) CACHE.get(beanType);

        return Objects.requireNonNull(
                projectionInterface,
                "No projection interface found for bean type: " + beanType
        );
    }

    private static Map<Class<?>, Class<?>> scanAll() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                return super.isCandidateComponent(beanDefinition) || beanDefinition.getMetadata().isAbstract();
            }
        };
        scanner.addIncludeFilter(new AnnotationTypeFilter(ProjectionInterface.class));

        var timedFind = EfFunctions.getTimed(() -> scanner.findCandidateComponents("echoflux"));
        var beanDefinitions = timedFind.getResult();
        log.info(
                "Scanned [{}] projection interfaces in [{}] ms",
                beanDefinitions.size(),
                timedFind.getDuration().toMillis()
        );

        return beanDefinitions.stream()
                .map(b -> Failable.get(() -> Class.forName(b.getBeanClassName())))
                .collect(
                        Collectors.toUnmodifiableMap(
                                b -> b.getAnnotation(ProjectionInterface.class).forBeanType(),
                                Function.identity()
                        )
                );
    }

}
