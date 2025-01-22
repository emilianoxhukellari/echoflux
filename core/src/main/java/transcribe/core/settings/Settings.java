package transcribe.core.settings;

import lombok.Builder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * A class annotated with {@link Settings} will be considered a setting. A setting holds information
 * that can be used at runtime to adjust the behavior of the application. Furthermore, a setting can be
 * changed at runtime without the need to restart the application.
 * </p>
 *
 * <p>
 * Annotating a class with {@link Settings} will automatically link the class to the storage layer.
 * </p>
 *
 * <p>
 * The source of truth for the settings schema is the code-base. The source of truth for the settings
 * values is the storage layer.
 * </p>
 *
 * <p>
 * For specifying default values, it is recommended to use {@link Builder} with {@link Builder.Default}
 * annotation on the fields that should have default values. Otherwise, the no args constructor will be used.
 * </p>
 *
 * <p>
 * The {@link Settings#key} can be any string, provided that it is unique across all settings.
 * </p>
 *
 * <p>
 * The following behavior is expected:
 * <ul>
 *     <li>Adding a new setting in the code-base creates a new setting with default values in the storage layer.</li>
 *     <li>Removing a setting from the code-base removes the setting from the storage layer.</li>
 *     <li>Adding a new field in the setting adds the new field with a default value in the storage layer.</li>
 *     <li>Removing a field from the setting removes this field from the storage layer.</li>
 * </ul>
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Settings {

    String key();

}