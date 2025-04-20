package echoflux.annotation.jpa;

import com.google.auto.service.AutoService;
import echoflux.annotation.projection.ProjectionInterfaceGenerator;
import echoflux.annotation.projection.ProjectionInterfaceSupport;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("echoflux.annotation.jpa.JpaDto")
@SupportedSourceVersion(SourceVersion.RELEASE_24)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class JpaDtoWithProjectionInterfaceSupportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(JpaDto.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .toList();

        for (var element : elements) {
            var jpaDto = element.getAnnotation(JpaDto.class);

            if (!jpaDto.withProjectionInterfaceSupport()) {
                continue;
            }

            if (element.getAnnotation(ProjectionInterfaceSupport.class) != null) {
                continue;
            }

            new ProjectionInterfaceGenerator(element, processingEnv)
                    .generate();
        }

        return false;
    }

}