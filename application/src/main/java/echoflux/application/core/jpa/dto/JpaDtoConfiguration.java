package echoflux.application.core.jpa.dto;

import lombok.Builder;
import org.apache.commons.lang3.Validate;
import echoflux.annotation.jpa.JpaDto;
import echoflux.core.core.bean.FieldProperty;
import echoflux.core.core.bean.MoreBeans;
import echoflux.core.core.utils.EfArrays;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Builder
public record JpaDtoConfiguration(Class<?> entityBeanType,
                                  Class<?> dtoBeanType,
                                  String idFieldName,
                                  List<String> auditFields,
                                  List<String> hiddenFields) {

    public JpaDtoConfiguration {
        Objects.requireNonNull(entityBeanType, "Entity bean type must not be null");
        Objects.requireNonNull(dtoBeanType, "DTO bean type must not be null");
        Objects.requireNonNull(idFieldName, "Id field name must not be null");
        Objects.requireNonNull(auditFields, "Audit fields must not be null");
        Objects.requireNonNull(hiddenFields, "Hidden fields must not be null");
    }

    public static JpaDtoConfiguration ofBeanType(Class<?> jpaDtoBeanType) {
        Objects.requireNonNull(jpaDtoBeanType, "Bean type must not be null");
        var annotation = Objects.requireNonNull(
                jpaDtoBeanType.getAnnotation(JpaDto.class),
                "Bean is not annotated with @JpaDto"
        );
        Validate.notBlank(annotation.idField(), "Id field name must not be blank");

        var byName = MoreBeans.getFieldPropertiesNested(jpaDtoBeanType)
                .stream()
                .collect(Collectors.groupingBy(FieldProperty::getName));

        for (int i = 0; i < annotation.auditFields().length; i++) {
            Validate.isTrue(byName.containsKey(annotation.auditFields()[i]),
                    "Audit field %s not found in bean %s",
                    annotation.auditFields()[i],
                    jpaDtoBeanType.getSimpleName()
            );
        }
        for (int i = 0; i < annotation.hiddenFields().length; i++) {
            Validate.isTrue(byName.containsKey(annotation.hiddenFields()[i]),
                    "Hidden field %s not found in bean %s",
                    annotation.hiddenFields()[i],
                    jpaDtoBeanType.getSimpleName()
            );
        }

        Validate.isTrue(byName.containsKey(annotation.idField()),
                "Id field %s not found in bean %s",
                annotation.idField(),
                jpaDtoBeanType.getSimpleName()
        );

        return JpaDtoConfiguration.builder()
                .entityBeanType(annotation.entityBeanType())
                .dtoBeanType(jpaDtoBeanType)
                .idFieldName(annotation.idField())
                .auditFields(EfArrays.toList(annotation.auditFields()))
                .hiddenFields(EfArrays.toList(annotation.hiddenFields()))
                .build();
    }

}