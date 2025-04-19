package echoflux.core.core.bean;

import lombok.Builder;
import lombok.With;
import echoflux.annotation.projection.AttributeProjectType;

@Builder
public record Attribute(@With String name, AttributeProjectType projectType) {
}