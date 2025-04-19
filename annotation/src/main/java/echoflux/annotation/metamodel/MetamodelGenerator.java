package echoflux.annotation.metamodel;

import com.google.common.base.CaseFormat;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.util.Objects;

public class MetamodelGenerator {

    private final TypeElement typeElement;
    private final ProcessingEnvironment processingEnv;

    public MetamodelGenerator(TypeElement typeElement, ProcessingEnvironment processingEnv) {
        Objects.requireNonNull(typeElement, "typeElement");
        Objects.requireNonNull(processingEnv, "processingEnv");

        this.typeElement = typeElement;
        this.processingEnv = processingEnv;
    }

    public void generate() {
        var elementUtils = processingEnv.getElementUtils();
        var packageName = elementUtils.getPackageOf(typeElement)
                .getQualifiedName()
                .toString();
        var metamodelClassName = typeElement.getSimpleName() + "_";

        try {
            generateMetamodel(packageName, metamodelClassName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate metamodel", e);
        }
    }

    private void generateMetamodel(String packageName, String metamodelClassName) throws IOException {
        var fileObject = processingEnv.getFiler()
                .createSourceFile(packageName + "." + metamodelClassName);

        try (var writer = fileObject.openWriter()) {
            var staticFields = new StringBuilder();
            for (var variableElement : ElementFilter.fieldsIn(typeElement.getEnclosedElements())) {
                var fieldName = variableElement.getSimpleName().toString();
                var staticFieldName = CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, fieldName);

                staticFields.append("\t")
                        .append("public static final String")
                        .append(" ")
                        .append(staticFieldName)
                        .append(" = \"")
                        .append(fieldName)
                        .append("\";\n");
            }

            var metamodelClassContent = """
                package %s;
                
                public abstract class %s {
                
                %s
                }
                """.formatted(packageName, metamodelClassName, staticFields);

            writer.write(metamodelClassContent);
        }

    }

}
