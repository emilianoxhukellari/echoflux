package echoflux.annotation.projection;

import org.apache.commons.lang3.StringUtils;
import echoflux.annotation.core.AttributeOverride;
import echoflux.annotation.core.ParentProperty;
import echoflux.annotation.core.ObjectConvertable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public final class ProjectionInterfaceGenerator {

    private final TypeElement sourceTypeElement;
    private final ProcessingEnvironment processingEnv;

    public ProjectionInterfaceGenerator(TypeElement sourceTypeElement, ProcessingEnvironment processingEnv) {
        this.sourceTypeElement = Objects.requireNonNull(sourceTypeElement, "sourceTypeElement must not be null");
        this.processingEnv = Objects.requireNonNull(processingEnv, "processingEnv must not be null");
    }

    public void generate() {
        var elementUtils = processingEnv.getElementUtils();
        var packageName = elementUtils.getPackageOf(sourceTypeElement)
                .getQualifiedName()
                .toString();
        var interfaceName = sourceTypeElement.getSimpleName() + "ProjectionInterface";

        try {
            generateInterface(packageName, interfaceName);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to generate interface", e);
        }
    }

    private void generateInterface(String packageName, String interfaceName) throws IOException {
        var fileObject = processingEnv.getFiler().createSourceFile(packageName + "." + interfaceName);
        try (var writer = fileObject.openWriter()) {

            var getterMethods = new StringBuilder();
            var toObjectMethod = new StringBuilder()
                    .append("\t@Override\n")
                    .append("\tdefault ")
                    .append(sourceTypeElement.getQualifiedName())
                    .append(" toObject() {\n")
                    .append("\t\tvar builder = ")
                    .append(sourceTypeElement.getQualifiedName())
                    .append(".builder")
                    .append("();\n");

            for (var variableElement : ElementFilter.fieldsIn(sourceTypeElement.getEnclosedElements())) {
                var attributeName = getAttributeName(variableElement);
                var getterName = "get" + StringUtils.capitalize(attributeName) + "()";
                var parentProperty = variableElement.getAnnotation(ParentProperty.class);

                var fieldType = parentProperty != null
                        ? getParentFieldType(variableElement)
                        : variableElement.asType().toString();

                getterMethods.append("\t")
                        .append(fieldType)
                        .append(" ")
                        .append(getterName)
                        .append(";\n");

                toObjectMethod.append("\t\tbuilder.")
                        .append(variableElement.getSimpleName().toString())
                        .append("(")
                        .append(getParameterExpression(parentProperty, getterName))
                        .append(");\n");
            }

            toObjectMethod.append("\n\t\treturn builder.build();\n\t}");

            var interfaceContent = """
                    package %s;

                    %s
                    public interface %s %s {

                    %s
                    %s

                    }
                    """.formatted(
                    packageName,
                    getProjectionInterfaceAnnotation(sourceTypeElement),
                    interfaceName,
                    getExtendsObjectConvertable(sourceTypeElement),
                    getterMethods,
                    toObjectMethod
            );

            writer.write(interfaceContent);
        }
    }

    private String getParentFieldType(VariableElement variableElement) {
        var parentClassElement = (TypeElement) processingEnv.getTypeUtils().asElement(variableElement.asType());

        return parentClassElement.getQualifiedName() + "ProjectionInterface";
    }

    private String getParameterExpression(ParentProperty parentProperty, String getterName) {
        return parentProperty != null
                ? getterName + " == null ? null : " + getterName + ".toObject()"
                : getterName;
    }

    private String getProjectionInterfaceAnnotation(TypeElement sourceTypeElement) {
        var annotationElement = processingEnv.getElementUtils()
                .getTypeElement(ProjectionInterface.class.getCanonicalName());
        return "@%s(forBeanType = %s.class)".formatted(
                annotationElement.getQualifiedName(),
                sourceTypeElement.getQualifiedName()
        );
    }

    private String getExtendsObjectConvertable(TypeElement sourceTypeElement) {
        var convertableElement = processingEnv.getElementUtils()
                .getTypeElement(ObjectConvertable.class.getCanonicalName());
        return "extends %s<%s>".formatted(
                convertableElement.getQualifiedName(),
                sourceTypeElement.getQualifiedName()
        );
    }

    private static String getAttributeName(VariableElement variableElement) {
        var attributeOverride = variableElement.getAnnotation(AttributeOverride.class);

        if (attributeOverride != null && StringUtils.isNotBlank(attributeOverride.name())) {
            return attributeOverride.name();
        }

        return variableElement.getSimpleName().toString();
    }

}
