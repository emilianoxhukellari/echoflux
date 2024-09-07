package transcribe.application.core.jpa.grid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JpaGridCrudActionsData {

    private boolean withCrudActions;

    @Builder.Default
    private List<String> excludedPropertiesList = List.of();

    public static JpaGridCrudActionsData empty() {
        return JpaGridCrudActionsData.builder()
                .withCrudActions(false)
                .build();
    }

}
