package echoflux.domain.template.data;

import echoflux.domain.core.data.BaseProjection;
import org.immutables.value.Value;

@Value.Immutable
public interface TemplateProjection extends BaseProjection<Long> {

    String getName();

    String getContent();

}
