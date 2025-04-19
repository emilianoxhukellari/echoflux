package echoflux.annotation.jpa;

import com.google.auto.service.AutoService;
import echoflux.annotation.metamodel.MetamodelGenerator;
import echoflux.annotation.metamodel.MetamodelSupport;

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
public class JpaDtoWithMetamodelSupportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(JpaDto.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .toList();

        for (var element : elements) {
            var jpaDto = element.getAnnotation(JpaDto.class);

            if (!jpaDto.withMetamodelSupport()) {
                continue;
            }

            if (element.getAnnotation(MetamodelSupport.class) != null) {
                continue;
            }

            new MetamodelGenerator(element, processingEnv)
                    .generate();
        }

        return false;
    }


}
