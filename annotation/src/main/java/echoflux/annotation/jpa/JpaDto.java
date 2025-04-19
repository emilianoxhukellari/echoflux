package echoflux.annotation.jpa;

import echoflux.annotation.metamodel.MetamodelSupport;
import echoflux.annotation.projection.ProjectionInterfaceSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface JpaDto {

    Class<?> entityBeanType();

    String idField() default JpaDtoConstants.ID_FIELD;

    String[] auditFields() default {
            JpaDtoConstants.CREATED_AT_FIELD,
            JpaDtoConstants.CREATED_BY_FIELD,
            JpaDtoConstants.UPDATED_AT_FIELD,
            JpaDtoConstants.UPDATED_BY_FIELD
    };

    String[] hiddenFields() default {};

    /**
     * Equivalent to using {@link ProjectionInterfaceSupport}.
     * */
    boolean withProjectionInterfaceSupport () default true;

    /**
     * Equivalent to using {@link MetamodelSupport}.
     * */
    boolean withMetamodelSupport() default true;

}