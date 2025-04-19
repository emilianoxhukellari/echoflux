package echoflux.annotation.metamodel;

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

@SupportedAnnotationTypes("echoflux.annotation.jpa.metamodel.MetamodelSupport")
@SupportedSourceVersion(SourceVersion.RELEASE_24)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class MetamodelSupportProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        var elements = roundEnv.getElementsAnnotatedWith(MetamodelSupport.class)
                .stream()
                .filter(e -> e.getKind() == ElementKind.CLASS)
                .map(TypeElement.class::cast)
                .toList();

        for (var element : elements) {
            new MetamodelGenerator(element, processingEnv).generate();
        }

        return true;
    }

}
