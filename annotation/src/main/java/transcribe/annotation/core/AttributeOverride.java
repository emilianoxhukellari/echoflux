package transcribe.annotation.core;

import org.apache.commons.lang3.StringUtils;
import transcribe.annotation.projection.AttributeProjectType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An attribute is by default the name of the field. This annotation allows to override the attribute name.
 * */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AttributeOverride {

    /**
     * If name is blank, the attribute name will be the field name.
     * */
    String name() default StringUtils.EMPTY;

    AttributeProjectType projectType() default AttributeProjectType.DEFAULT;

}
