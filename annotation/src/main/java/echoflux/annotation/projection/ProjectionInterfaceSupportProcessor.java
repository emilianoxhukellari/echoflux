package echoflux.annotation.projection;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("echoflux.annotation.jpa.projection.ProjectionInterfaceSupport")
@SupportedSourceVersion(SourceVersion.RELEASE_24)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class ProjectionInterfaceSupportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(ProjectionInterfaceSupport.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .toList();

        for (var element : elements) {
            new ProjectionInterfaceGenerator(element, processingEnv).generate();
        }

        return true;
    }

}
