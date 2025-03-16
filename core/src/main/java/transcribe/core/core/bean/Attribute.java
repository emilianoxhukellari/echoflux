package transcribe.core.core.bean;

import lombok.Builder;
import lombok.With;
import transcribe.annotation.projection.AttributeProjectType;

@Builder
public record Attribute(@With String name, AttributeProjectType projectType) {
}